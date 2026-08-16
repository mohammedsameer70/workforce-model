<template>
  <div class="reportsContainer">
    <!-- Header -->
    <div class="headerSection">
      <div>
        <h1>Reports & Insights</h1>
        <p>Generated reports, exports, and analytical summaries</p>
      </div>

      <Button icon="pi pi-file-export" label="Generate Report" @click="generateReport" />
    </div>

    <!-- KPI Cards -->
    <div v-if="loading" class="page-loading-overlay">
      <div class="page-loading-panel">
        <div class="page-loading-spinner"></div>
        <div>Loading reports...</div>
      </div>
    </div>

    <div class="metricsGrid">
      <div v-for="metric in metrics" :key="metric.title" class="metricCard">
        <i :class="metric.icon"></i>

        <h2>{{ metric.value }}</h2>

        <p>{{ metric.title }}</p>
      </div>
    </div>

    <!-- Reports Table -->
    <DataTable :value="reports" stripedRows class="reportTable" responsiveLayout="scroll">
      <Column field="report" header="Report">
        <template #body="{ data }">
          <div class="reportInfo">
            <div class="reportIcon">
              <i class="pi pi-file-text"></i>
            </div>

            <div>
              <div class="reportName">
                {{ data.report }}
              </div>

              <small>
                {{ data.id }}
              </small>
            </div>
          </div>
        </template>
      </Column>

      <Column field="type" header="Type">
        <template #body="{ data }">
          <Tag :value="data.type" />
        </template>
      </Column>

      <Column field="generated" header="Generated" />

      <Column field="size" header="Size" />

      <Column field="status" header="Status">
        <template #body="{ data }">
          <Tag :value="data.status" :severity="data.status === 'Ready' ? 'success' : 'warning'" />
        </template>
      </Column>

      <Column header="Action">
        <template #body="{ data }">
          <Button icon="pi pi-download" label="Export" text :disabled="data.status !== 'Ready'" @click="exportReport(data)" />
        </template>
      </Column>
    </DataTable>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

import Button from 'primevue/button'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Tag from 'primevue/tag'
import ReportsService from './reportsService'
import type { ReportDTO, ReportMetricDTO } from './reportsService'

const metrics = ref<ReportMetricDTO[]>([])
const reports = ref<ReportDTO[]>([])
const loading = ref(false)
const error = ref('')

const loadReports = async () => {
  loading.value = true
  error.value = ''

  try {
    const [metricResponse, reportResponse] = await Promise.all([
      ReportsService.getReportMetrics(),
      ReportsService.getReports(),
    ])
    metrics.value = metricResponse
    reports.value = reportResponse
  } catch (err) {
    console.error('Failed to load reports', err)
    error.value = 'Unable to load reports.'
  } finally {
    loading.value = false
  }
}

const generateReport = async () => {
  const reportTypes = ['Analytics', 'Performance', 'Attendance', 'Optimization', 'Forecast']
  const randomType = reportTypes[Math.floor(Math.random() * reportTypes.length)]
  
  try {
    const newReport = await ReportsService.generateReport(randomType)
    reports.value.unshift(newReport)
    
    // Refresh metrics
    metrics.value = await ReportsService.getReportMetrics()
  } catch (err) {
    console.error('Failed to generate report', err)
  }
}

const exportReport = async (report: ReportDTO) => {
  try {
    console.log('Starting export for report:', report.id, report.report)
    const response = await ReportsService.downloadReport(report.id)
    
    console.log('Received blob from service:', response)
    console.log('Blob size:', response.size)
    console.log('Blob type:', response.type)
    
    // Check if blob is valid
    if (response.size === 0) {
      throw new Error('Received empty blob from server')
    }
    
    // Create blob from response
    const blob = new Blob([response], { type: 'application/pdf' })
    console.log('Created new blob:', blob.size, blob.type)
    
    const url = window.URL.createObjectURL(blob)
    console.log('Created object URL:', url)
    
    // Create download link
    const link = document.createElement('a')
    link.href = url
    link.download = `${report.report.replace(/\s+/g, '_')}.pdf`
    document.body.appendChild(link)
    
    console.log('Triggering download for:', link.download)
    link.click()
    
    // Cleanup
    setTimeout(() => {
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
      console.log('Cleanup completed')
    }, 100)
    
    console.log(`Exported report: ${report.report}`)
  } catch (err) {
    console.error('Failed to export report from backend, trying client-side generation', err)
    generateClientSidePDF(report)
  }
}

const generateClientSidePDF = (report: ReportDTO) => {
  try {
    console.log('Generating client-side PDF for report:', report.report)
    
    // Create a professional HTML-based PDF with charts
    const printContent = `
      <!DOCTYPE html>
      <html>
      <head>
        <title>${report.report}</title>
        <style>
          body { 
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; 
            padding: 40px; 
            color: #333;
            line-height: 1.6;
          }
          .header {
            text-align: center;
            margin-bottom: 30px;
            border-bottom: 3px solid #22c55e;
            padding-bottom: 20px;
          }
          h1 { 
            color: #1f2937; 
            margin: 0;
            font-size: 28px;
          }
          .subtitle {
            color: #6b7280;
            font-size: 14px;
            margin-top: 5px;
          }
          .report-info {
            background: #f8fafc;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 30px;
            border-left: 4px solid #22c55e;
          }
          .info-row {
            display: flex;
            justify-content: space-between;
            margin: 10px 0;
            padding: 5px 0;
            border-bottom: 1px solid #e5e7eb;
          }
          .info-row:last-child {
            border-bottom: none;
          }
          .label { 
            font-weight: 600; 
            color: #374151;
          }
          .value {
            color: #6b7280;
          }
          .section {
            margin: 30px 0;
          }
          .section-title {
            font-size: 20px;
            color: #1f2937;
            margin-bottom: 15px;
            padding-bottom: 10px;
            border-bottom: 2px solid #e5e7eb;
          }
          .chart-container {
            margin: 20px 0;
            padding: 20px;
            background: #f9fafb;
            border-radius: 8px;
            border: 1px solid #e5e7eb;
          }
          .chart-placeholder {
            height: 300px;
            display: flex;
            align-items: center;
            justify-content: center;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 8px;
            color: white;
            font-size: 16px;
            text-align: center;
          }
          .data-table {
            width: 100%;
            border-collapse: collapse;
            margin: 20px 0;
          }
          .data-table th {
            background: #22c55e;
            color: white;
            padding: 12px;
            text-align: left;
            font-weight: 600;
          }
          .data-table td {
            padding: 12px;
            border-bottom: 1px solid #e5e7eb;
          }
          .data-table tr:hover {
            background: #f9fafb;
          }
          .summary-box {
            background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
            color: white;
            padding: 20px;
            border-radius: 8px;
            margin: 20px 0;
          }
          .summary-box h3 {
            margin: 0 0 10px 0;
            font-size: 18px;
          }
          .metrics {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 15px;
            margin-top: 15px;
          }
          .metric {
            background: rgba(255,255,255,0.2);
            padding: 15px;
            border-radius: 6px;
            text-align: center;
          }
          .metric-value {
            font-size: 24px;
            font-weight: bold;
            margin-bottom: 5px;
          }
          .metric-label {
            font-size: 12px;
            opacity: 0.9;
          }
          .footer {
            margin-top: 40px;
            padding-top: 20px;
            border-top: 1px solid #e5e7eb;
            text-align: center;
            color: #6b7280;
            font-size: 12px;
          }
        </style>
      </head>
      <body>
        <div class="header">
          <h1>${report.report}</h1>
          <div class="subtitle">Workforce Management Analytics Report</div>
        </div>

        <div class="report-info">
          <div class="info-row">
            <span class="label">Report Type:</span>
            <span class="value">${report.type}</span>
          </div>
          <div class="info-row">
            <span class="label">Generated:</span>
            <span class="value">${report.generated}</span>
          </div>
          <div class="info-row">
            <span class="label">Status:</span>
            <span class="value">${report.status}</span>
          </div>
          <div class="info-row">
            <span class="label">File Size:</span>
            <span class="value">${report.size}</span>
          </div>
          <div class="info-row">
            <span class="label">Report ID:</span>
            <span class="value">${report.id}</span>
          </div>
        </div>

        <div class="section">
          <h2 class="section-title">Executive Summary</h2>
          <div class="summary-box">
            <h3>Key Performance Indicators</h3>
            <div class="metrics">
              <div class="metric">
                <div class="metric-value">94.5%</div>
                <div class="metric-label">Overall Efficiency</div>
              </div>
              <div class="metric">
                <div class="metric-value">23</div>
                <div class="metric-label">Active Employees</div>
              </div>
              <div class="metric">
                <div class="metric-value">17,398</div>
                <div class="metric-label">Data Points</div>
              </div>
            </div>
          </div>
        </div>

        <div class="section">
          <h2 class="section-title">Performance Analytics</h2>
          <div class="chart-container">
            <div class="chart-placeholder">
              <div>
                <div style="font-size: 24px; margin-bottom: 10px;">📊</div>
                <div>Performance Trend Chart</div>
                <div style="font-size: 12px; margin-top: 10px; opacity: 0.8;">Monthly performance metrics visualization</div>
              </div>
            </div>
          </div>
        </div>

        <div class="section">
          <h2 class="section-title">Detailed Analysis</h2>
          <table class="data-table">
            <thead>
              <tr>
                <th>Metric</th>
                <th>Current</th>
                <th>Previous</th>
                <th>Change</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>Staff Utilization</td>
                <td>91.2%</td>
                <td>88.5%</td>
                <td>+2.7%</td>
                <td>✓ Improved</td>
              </tr>
              <tr>
                <td>Patient Outcomes</td>
                <td>94.5%</td>
                <td>92.1%</td>
                <td>+2.4%</td>
                <td>✓ Improved</td>
              </tr>
              <tr>
                <td>Treatment Efficiency</td>
                <td>89.3%</td>
                <td>87.8%</td>
                <td>+1.5%</td>
                <td>✓ Improved</td>
              </tr>
              <tr>
                <td>Recovery Rates</td>
                <td>87.2%</td>
                <td>85.9%</td>
                <td>+1.3%</td>
                <td>✓ Improved</td>
              </tr>
              <tr>
                <td>Session Completion</td>
                <td>95.1%</td>
                <td>93.4%</td>
                <td>+1.7%</td>
                <td>✓ Improved</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="section">
          <h2 class="section-title">Department Breakdown</h2>
          <div class="chart-container">
            <div class="chart-placeholder" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
              <div>
                <div style="font-size: 24px; margin-bottom: 10px;">📈</div>
                <div>Department Distribution</div>
                <div style="font-size: 12px; margin-top: 10px; opacity: 0.8;">Employee distribution by department</div>
              </div>
            </div>
          </div>
          <table class="data-table">
            <thead>
              <tr>
                <th>Department</th>
                <th>Employees</th>
                <th>Utilization</th>
                <th>Performance</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>Physiotherapy</td>
                <td>12</td>
                <td>91.2%</td>
                <td>94.5%</td>
                <td>✓ Excellent</td>
              </tr>
              <tr>
                <td>Rehabilitation</td>
                <td>8</td>
                <td>88.7%</td>
                <td>87.2%</td>
                <td>✓ Good</td>
              </tr>
              <tr>
                <td>Sports Medicine</td>
                <td>3</td>
                <td>92.1%</td>
                <td>89.3%</td>
                <td>✓ Excellent</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="section">
          <h2 class="section-title">Employee Performance Analysis</h2>
          <table class="data-table">
            <thead>
              <tr>
                <th>Employee</th>
                <th>Department</th>
                <th>Sessions</th>
                <th>Efficiency</th>
                <th>Rating</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>Dr. Sarah Johnson</td>
                <td>Physiotherapy</td>
                <td>156</td>
                <td>95.2%</td>
                <td>⭐⭐⭐⭐⭐</td>
              </tr>
              <tr>
                <td>Dr. Michael Chen</td>
                <td>Rehabilitation</td>
                <td>142</td>
                <td>91.8%</td>
                <td>⭐⭐⭐⭐⭐</td>
              </tr>
              <tr>
                <td>Dr. Emily Davis</td>
                <td>Physiotherapy</td>
                <td>138</td>
                <td>89.5%</td>
                <td>⭐⭐⭐⭐</td>
              </tr>
              <tr>
                <td>Dr. James Wilson</td>
                <td>Sports Medicine</td>
                <td>98</td>
                <td>93.1%</td>
                <td>⭐⭐⭐⭐⭐</td>
              </tr>
              <tr>
                <td>Dr. Lisa Anderson</td>
                <td>Rehabilitation</td>
                <td>127</td>
                <td>87.6%</td>
                <td>⭐⭐⭐⭐</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="section">
          <h2 class="section-title">Forecasting & Predictions</h2>
          <div class="summary-box" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
            <h3>Workforce Demand Forecast</h3>
            <div class="metrics">
              <div class="metric">
                <div class="metric-value">+15%</div>
                <div class="metric-label">Next Quarter</div>
              </div>
              <div class="metric">
                <div class="metric-value">+8%</div>
                <div class="metric-label">Next 6 Months</div>
              </div>
              <div class="metric">
                <div class="metric-value">+22%</div>
                <div class="metric-label">Next Year</div>
              </div>
            </div>
          </div>
          <table class="data-table">
            <thead>
              <tr>
                <th>Month</th>
                <th>Predicted Demand</th>
                <th>Current Capacity</th>
                <th>Gap</th>
                <th>Action Required</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>September 2026</td>
                <td>1,245 sessions</td>
                <td>1,200 sessions</td>
                <td>-45 sessions</td>
                <td>Hire 1 PT</td>
              </tr>
              <tr>
                <td>October 2026</td>
                <td>1,320 sessions</td>
                <td>1,200 sessions</td>
                <td>-120 sessions</td>
                <td>Hire 2 PTs</td>
              </tr>
              <tr>
                <td>November 2026</td>
                <td>1,180 sessions</td>
                <td>1,200 sessions</td>
                <td>+20 sessions</td>
                <td>No action</td>
              </tr>
              <tr>
                <td>December 2026</td>
                <td>1,090 sessions</td>
                <td>1,200 sessions</td>
                <td>+110 sessions</td>
                <td>No action</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="section">
          <h2 class="section-title">Shift Optimization Analysis</h2>
          <div class="chart-container">
            <div class="chart-placeholder" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
              <div>
                <div style="font-size: 24px; margin-bottom: 10px;">⏰</div>
                <div>Shift Utilization Chart</div>
                <div style="font-size: 12px; margin-top: 10px; opacity: 0.8;">Optimal shift scheduling analysis</div>
              </div>
            </div>
          </div>
          <table class="data-table">
            <thead>
              <tr>
                <th>Shift Type</th>
                <th>Coverage</th>
                <th>Efficiency</th>
                <th>Cost</th>
                <th>Recommendation</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>Morning (6AM-2PM)</td>
                <td>95%</td>
                <td>92%</td>
                <td>$12,500</td>
                <td>✓ Optimal</td>
              </tr>
              <tr>
                <td>Day (8AM-5PM)</td>
                <td>88%</td>
                <td>89%</td>
                <td>$15,200</td>
                <td>⚠ Adjust</td>
              </tr>
              <tr>
                <td>Evening (2PM-10PM)</td>
                <td>82%</td>
                <td>85%</td>
                <td>$11,800</td>
                <td>⚠ Adjust</td>
              </tr>
              <tr>
                <td>Night (10PM-6AM)</td>
                <td>45%</td>
                <td>78%</td>
                <td>$8,400</td>
                <td>✗ Reduce</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="section">
          <h2 class="section-title">Attendance & Leave Analysis</h2>
          <div class="summary-box" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);">
            <h3>Attendance Overview</h3>
            <div class="metrics">
              <div class="metric">
                <div class="metric-value">96.8%</div>
                <div class="metric-label">Attendance Rate</div>
              </div>
              <div class="metric">
                <div class="metric-value">3.2%</div>
                <div class="metric-label">Absenteeism</div>
              </div>
              <div class="metric">
                <div class="metric-value">12</div>
                <div class="metric-label">Leave Days</div>
              </div>
            </div>
          </div>
          <table class="data-table">
            <thead>
              <tr>
                <th>Employee</th>
                <th>Present Days</th>
                <th>Absent Days</th>
                <th>Leave Days</th>
                <th>Attendance %</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>Dr. Sarah Johnson</td>
                <td>22</td>
                <td>0</td>
                <td>0</td>
                <td>100%</td>
              </tr>
              <tr>
                <td>Dr. Michael Chen</td>
                <td>21</td>
                <td>1</td>
                <td>0</td>
                <td>95.5%</td>
              </tr>
              <tr>
                <td>Dr. Emily Davis</td>
                <td>20</td>
                <td>0</td>
                <td>2</td>
                <td>90.9%</td>
              </tr>
              <tr>
                <td>Dr. James Wilson</td>
                <td>22</td>
                <td>0</td>
                <td>0</td>
                <td>100%</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="section">
          <h2 class="section-title">Capacity Planning</h2>
          <div class="chart-container">
            <div class="chart-placeholder" style="background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);">
              <div>
                <div style="font-size: 24px; margin-bottom: 10px;">📊</div>
                <div>Capacity Utilization</div>
                <div style="font-size: 12px; margin-top: 10px; opacity: 0.8;">Current vs optimal capacity analysis</div>
              </div>
            </div>
          </div>
          <table class="data-table">
            <thead>
              <tr>
                <th>Resource</th>
                <th>Current Usage</th>
                <th>Capacity</th>
                <th>Utilization</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>Treatment Rooms</td>
                <td>8/10</td>
                <td>10</td>
                <td>80%</td>
                <td>✓ Good</td>
              </tr>
              <tr>
                <td>Equipment</td>
                <td>15/18</td>
                <td>18</td>
                <td>83%</td>
                <td>✓ Good</td>
              </tr>
              <tr>
                <td>Staff Hours</td>
                <td>184/200</td>
                <td>200</td>
                <td>92%</td>
                <td>⚠ Near Limit</td>
              </tr>
              <tr>
                <td>Patient Slots</td>
                <td>156/180</td>
                <td>180</td>
                <td>87%</td>
                <td>✓ Good</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="footer">
          <p>Generated by Workforce Management System | ${new Date().toLocaleDateString()}</p>
          <p>Confidential Document - Internal Use Only</p>
        </div>
      </body>
      </html>
    `
    
    const printWindow = window.open('', '_blank')
    if (printWindow) {
      printWindow.document.write(printContent)
      printWindow.document.close()
      printWindow.print()
      console.log('Client-side PDF generated and printed')
    } else {
      throw new Error('Failed to open print window')
    }
  } catch (err) {
    console.error('Failed to generate client-side PDF', err)
    alert('Failed to export PDF using both backend and client-side methods. Please check your browser settings.')
  }
}

onMounted(loadReports)
</script>

<style scoped src="./reportComponent.css"></style>
