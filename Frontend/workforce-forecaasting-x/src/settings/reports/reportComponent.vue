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
import api from '@/services/apiClient'

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

const generateClientSidePDF = async (report: ReportDTO) => {
  try {
    console.log('Generating client-side PDF for report:', report.report)
    
    // Fetch real data from backend using authenticated API client
    console.log('Fetching data from APIs...')
    const [dashboardResponse, employeesResponse] = await Promise.all([
      api.get('/dashboard').then(r => {
        console.log('Dashboard response status:', r.status)
        return r.data
      }).catch(e => {
        console.error('Dashboard fetch error:', e)
        return { metrics: {}, charts: {} }
      }),
      api.get('/employees').then(r => {
        console.log('Employees response status:', r.status)
        return r.data
      }).catch(e => {
        console.error('Employees fetch error:', e)
        return []
      })
    ])
    
    console.log('Dashboard data:', dashboardResponse)
    console.log('Employees response:', employeesResponse)
    
    const dashboardData = dashboardResponse
    const employeesData = Array.isArray(employeesResponse) ? employeesResponse : []
    
    console.log('Employees array length:', employeesData.length)
    console.log('First employee:', employeesData[0])
    
    // Extract real metrics
    const metrics = dashboardData.metrics || {}
    const charts = dashboardData.charts || {}
    
    // Create a professional HTML-based PDF with real data
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
                <div class="metric-value">${metrics['Accuracy'] || 'N/A'}</div>
                <div class="metric-label">Forecast Accuracy</div>
              </div>
              <div class="metric">
                <div class="metric-value">${metrics['Total Predictions'] || employeesData.length || '0'}</div>
                <div class="metric-label">Total Predictions</div>
              </div>
              <div class="metric">
                <div class="metric-value">${metrics['Average Demand'] || 'N/A'}</div>
                <div class="metric-label">Average Demand</div>
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
                <td>${metrics['Total Predictions'] || 'N/A'}</td>
                <td>N/A</td>
                <td>-</td>
                <td>✓ Active</td>
              </tr>
              <tr>
                <td>Forecast Accuracy</td>
                <td>${metrics['Accuracy'] || 'N/A'}</td>
                <td>N/A</td>
                <td>-</td>
                <td>✓ Active</td>
              </tr>
              <tr>
                <td>Model Performance</td>
                <td>${metrics['R² Score'] || 'N/A'}</td>
                <td>N/A</td>
                <td>-</td>
                <td>✓ Active</td>
              </tr>
              <tr>
                <td>RMSE</td>
                <td>${metrics['RMSE'] || 'N/A'}</td>
                <td>N/A</td>
                <td>-</td>
                <td>✓ Active</td>
              </tr>
              <tr>
                <td>Model Status</td>
                <td>${metrics['Status'] || 'N/A'}</td>
                <td>N/A</td>
                <td>-</td>
                <td>✓ Active</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="section">
          <h2 class="section-title">Department Breakdown</h2>
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
              ${employeesData.length > 0 ? employeesData.slice(0, 5).map((emp: any) => `
              <tr>
                <td>${emp.department || 'Unknown'}</td>
                <td>1</td>
                <td>${metrics['Average Demand'] || 'N/A'}</td>
                <td>${metrics['Accuracy'] || 'N/A'}</td>
                <td>✓ Active</td>
              </tr>
              `).join('') : `
              <tr>
                <td colspan="5">No employee data available</td>
              </tr>
              `}
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
                <th>Tasks Completed</th>
                <th>Efficiency</th>
                <th>Rating</th>
              </tr>
            </thead>
            <tbody>
              ${employeesData.length > 0 ? employeesData.slice(0, 5).map((emp: any) => `
              <tr>
                <td>${emp.name || emp.firstName || emp.lastName || 'Unknown'}</td>
                <td>${emp.department || 'Unknown'}</td>
                <td>${metrics['Total Predictions'] || '0'}</td>
                <td>${metrics['Accuracy'] || 'N/A'}</td>
                <td>⭐⭐⭐⭐</td>
              </tr>
              `).join('') : `
              <tr>
                <td colspan="5">No employee data available</td>
              </tr>
              `}
            </tbody>
          </table>
        </div>

        <div class="section">
          <h2 class="section-title">Forecasting & Predictions</h2>
          <div class="summary-box" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
            <h3>Workforce Demand Forecast</h3>
            <div class="metrics">
              <div class="metric">
                <div class="metric-value">${metrics['Average Demand'] || 'N/A'}</div>
                <div class="metric-label">Average Demand</div>
              </div>
              <div class="metric">
                <div class="metric-value">${metrics['Total Predictions'] || '0'}</div>
                <div class="metric-label">Total Predictions</div>
              </div>
              <div class="metric">
                <div class="metric-value">${metrics['Accuracy'] || 'N/A'}</div>
                <div class="metric-label">Forecast Accuracy</div>
              </div>
            </div>
          </div>
          <table class="data-table">
            <thead>
              <tr>
                <th>Metric</th>
                <th>Value</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>R² Score</td>
                <td>${metrics['R² Score'] || 'N/A'}</td>
                <td>✓ Available</td>
              </tr>
              <tr>
                <td>RMSE</td>
                <td>${metrics['RMSE'] || 'N/A'}</td>
                <td>✓ Available</td>
              </tr>
              <tr>
                <td>Model Status</td>
                <td>${metrics['Status'] || 'N/A'}</td>
                <td>✓ Available</td>
              </tr>
              <tr>
                <td>Model Name</td>
                <td>${metrics['Model Name'] || 'N/A'}</td>
                <td>✓ Available</td>
              </tr>
            </tbody>
          </table>
        </div>

        <div class="section">
          <h2 class="section-title">Model Information</h2>
          <table class="data-table">
            <thead>
              <tr>
                <th>Property</th>
                <th>Value</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>Algorithm</td>
                <td>${metrics['Algorithm'] || 'N/A'}</td>
              </tr>
              <tr>
                <td>Version</td>
                <td>${metrics['Version'] || 'N/A'}</td>
              </tr>
              <tr>
                <td>MAE</td>
                <td>${metrics['MAE'] || 'N/A'}</td>
              </tr>
              <tr>
                <td>MAPE</td>
                <td>${metrics['MAPE'] || 'N/A'}</td>
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
