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
          <Button icon="pi pi-download" label="Export" text :disabled="data.status !== 'Ready'" />
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

onMounted(loadReports)
</script>

<style scoped src="./reportComponent.css"></style>
