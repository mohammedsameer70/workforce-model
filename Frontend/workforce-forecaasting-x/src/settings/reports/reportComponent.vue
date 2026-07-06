<template>
  <div class="reportsContainer">
    <!-- Header -->
    <div class="headerSection">
      <div>
        <h1>Reports & Insights</h1>
        <p>Generated reports, exports, and analytical summaries</p>
      </div>

      <Button icon="pi pi-file-export" label="Generate Report" />
    </div>

    <!-- KPI Cards -->
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
import { ref } from 'vue'

import Button from 'primevue/button'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import Tag from 'primevue/tag'

const metrics = ref([
  {
    value: '8',
    title: 'Total Reports',
    icon: 'pi pi-file',
  },
  {
    value: '7',
    title: 'Ready for Download',
    icon: 'pi pi-check-circle',
  },
  {
    value: '1',
    title: 'Generating',
    icon: 'pi pi-spin pi-spinner',
  },
  {
    value: '24.8',
    title: 'Total Size (MB)',
    icon: 'pi pi-database',
  },
])

const reports = ref([
  {
    id: 'RPT-001',
    report: 'Weekly Workforce Demand Report',
    type: 'Forecasting',
    generated: '2024-01-15 09:00',
    size: '2.4 MB',
    status: 'Ready',
  },
  {
    id: 'RPT-002',
    report: 'Shift Optimization Summary',
    type: 'Scheduling',
    generated: '2024-01-15 08:30',
    size: '1.8 MB',
    status: 'Ready',
  },
  {
    id: 'RPT-003',
    report: 'Employee Utilization Report',
    type: 'HR Analytics',
    generated: '2024-01-14 18:00',
    size: '3.1 MB',
    status: 'Ready',
  },
  {
    id: 'RPT-004',
    report: 'Operational KPI Dashboard Export',
    type: 'Operations',
    generated: '2024-01-14 12:00',
    size: '5.6 MB',
    status: 'Ready',
  },
  {
    id: 'RPT-005',
    report: 'ML Model Performance Report',
    type: 'Data Science',
    generated: '2024-01-13 16:00',
    size: '1.2 MB',
    status: 'Ready',
  },
  {
    id: 'RPT-006',
    report: 'Infrastructure Benchmark Report',
    type: 'DevOps',
    generated: '2024-01-13 14:00',
    size: '4.3 MB',
    status: 'Generating',
  },
  {
    id: 'RPT-007',
    report: 'Monthly Capacity Planning',
    type: 'Planning',
    generated: '2024-01-12 10:00',
    size: '2.9 MB',
    status: 'Ready',
  },
  {
    id: 'RPT-008',
    report: 'Department Attendance Analysis',
    type: 'HR Analytics',
    generated: '2024-01-11 09:00',
    size: '1.5 MB',
    status: 'Ready',
  },
])
</script>

<style scoped src="./reportComponent.css"></style>
