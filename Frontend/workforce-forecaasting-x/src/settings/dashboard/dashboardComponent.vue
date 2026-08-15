<template>
  <div class="dashboard">
    <h1>{{ lbl.operationsCommandCenter }}</h1>
    <p>{{ lbl.operationsCommandCenterDesc }}</p>
  </div>

  <div v-if="loading" class="page-loading-overlay">
    <div class="page-loading-panel">
      <div class="page-loading-spinner"></div>
      <div>Loading dashboard data...</div>
    </div>
  </div>

  <!-- KPI CARDS -->
  <div class="cardsInfo">
    <div class="card" v-for="metric in metrics" :key="metric.title">
      <div class="iconBox">
        <i :class="metric.icon" class="icon"></i>
      </div>

      <h3>{{ metric.title }}</h3>
      <p>{{ metric.value }}</p>
    </div>
  </div>

  <!-- CHART -->
  <Panel :header="lbl.workforce" class="mb-4">
    <div class="workforceSection">
      <div class="workforceChart">
        <div class="chartCard">
          <Chart
            type="line"
            :data="chartData"
            :options="chartOptions"
            style="width: 100%; height: 100%"
          />
        </div>
      </div>

      <div class="alertsPanel">
        <div class="alertsHeader">
          <h3>Operational Alerts</h3>
          <span class="criticalCount">2 Critical</span>
        </div>

        <div
          v-for="(alert, index) in alerts"
          :key="index"
          class="alertItem"
          :class="alert.severity"
        >
          <div class="alertIcon">
            <i :class="alert.icon"></i>
          </div>

          <div class="alertContent">
            <p class="alertTitle">
              {{ alert.title }}
            </p>

            <div class="alertMeta">
              <span>{{ alert.time }}</span>
              <span>{{ alert.department }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </Panel>
  <Panel header="Department Performance" class="mb-4">
    <div class="departmentSection">
      <div class="chartCard">
        <Chart
          type="bar"
          :data="barChartData"
          :options="barChartOptions"
          style="width: 100%; height: 100%"
        />
      </div>
    </div>
  </Panel>
  <Panel header="Department Staffing Heatmap" class="mb-4">
    <p class="tableSubTitle">Current shift allocation by department</p>

    <DataTable :value="staffingData" stripedRows showGridlines responsiveLayout="scroll">
      <Column field="department" header="Department"></Column>

      <Column field="morning" header="Morning">
        <template #body="{ data }">
          <span class="shiftCell morning">
            {{ data.morning }}
          </span>
        </template>
      </Column>

      <Column field="afternoon" header="Afternoon">
        <template #body="{ data }">
          <span class="shiftCell afternoon">
            {{ data.afternoon }}
          </span>
        </template>
      </Column>

      <Column field="night" header="Night">
        <template #body="{ data }">
          <span class="shiftCell night">
            {{ data.night }}
          </span>
        </template>
      </Column>

      <Column field="total" header="Total">
        <template #body="{ data }">
          <strong>{{ data.total }}</strong>
        </template>
      </Column>
    </DataTable>
  </Panel>
  <!-- BAR CHART -->
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import Chart from 'primevue/chart'
import Panel from 'primevue/panel'
import { lbl } from '@/assets/constants/labels'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'

import {
  Chart as ChartJS,
  LineElement,
  BarElement,
  BarController,
  CategoryScale,
  LinearScale,
  PointElement,
  Tooltip,
  Legend,
} from 'chart.js'
import CLDashboardService from '../aiModels/dashboardService'

ChartJS.register(
  LineElement,
  BarElement,
  BarController,
  CategoryScale,
  LinearScale,
  PointElement,
  Tooltip,
  Legend,
)

let refreshInterval: number | null = null

const staffingData = ref([
  {
    department: 'Inbound',
    morning: 22,
    afternoon: 40,
    night: 19,
    total: 81,
  },
  {
    department: 'Outbound',
    morning: 33,
    afternoon: 31,
    night: 20,
    total: 84,
  },
  {
    department: 'Sortation',
    morning: 38,
    afternoon: 53,
    night: 23,
    total: 114,
  },
  {
    department: 'Packing',
    morning: 39,
    afternoon: 52,
    night: 28,
    total: 119,
  },
  {
    department: 'Returns',
    morning: 27,
    afternoon: 28,
    night: 29,
    total: 84,
  },
  {
    department: 'Quality Control',
    morning: 34,
    afternoon: 60,
    night: 19,
    total: 113,
  },
])

const alerts = ref([
  {
    severity: 'critical',
    title: 'Outbound department understaffed by 12 workers for afternoon shift',
    time: '2 min ago',
    department: 'Outbound',
    icon: 'pi pi-users',
  },
  {
    severity: 'warning',
    title: 'analytics-engine service CPU usage above 65% threshold',
    time: '8 min ago',
    department: 'System',
    icon: 'pi pi-server',
  },
  {
    severity: 'success',
    title: 'ML model v3.2 deployed successfully — forecast accuracy improved to 94.2%',
    time: '15 min ago',
    department: 'ML Pipeline',
    icon: 'pi pi-chart-line',
  },
  {
    severity: 'warning',
    title: 'Projected overtime breach: Packing dept. 14:00-22:00 shift',
    time: '22 min ago',
    department: 'Packing',
    icon: 'pi pi-clock',
  },
  {
    severity: 'success',
    title: 'Shift optimization completed for next 7-day window',
    time: '35 min ago',
    department: 'Planning',
    icon: 'pi pi-calendar',
  },
  {
    severity: 'critical',
    title: 'Night shift coverage at 68% — below 75% minimum threshold',
    time: '1 hour ago',
    department: 'Operations',
    icon: 'pi pi-exclamation-triangle',
  },
])
/* ---------------- KPI METRICS ---------------- */
const metrics = ref([
  {
    title: 'Active Workforce',
    value: 'Loading...',
    change: '0%',
    rating: 'medium',
    icon: 'pi pi-users',
  },
  {
    title: 'Forecast Accuracy',
    value: 'Loading...',
    change: '0%',
    rating: 'medium',
    icon: 'pi pi-chart-line',
  },
  { title: 'Shift Utilization', value: 'Loading...', change: '0%', rating: 'medium', icon: 'pi pi-clock' },
  {
    title: 'Total Predictions',
    value: 'Loading...',
    change: '0%',
    rating: 'medium',
    icon: 'pi pi-list',
  },
  {
    title: 'Average Demand',
    value: 'Loading...',
    change: '0%',
    rating: 'medium',
    icon: 'pi pi-box',
  },
  {
    title: 'Model Status',
    value: 'Loading...',
    change: '',
    rating: 'medium',
    icon: 'pi pi-cog',
  },
  {
    title: 'Model Name',
    value: 'Loading...',
    change: '',
    rating: 'medium',
    icon: 'pi pi-database',
  },
  {
    title: 'R² Score',
    value: 'Loading...',
    change: '0%',
    rating: 'medium',
    icon: 'pi pi-chart-bar',
  },
  { title: 'RMSE', value: 'Loading...', change: '', rating: 'medium', icon: 'pi pi-chart-bar' },
])

const loading = ref(true)

const fetchDashboardMetrics = async () => {
  loading.value = true
  try {
    const response = await CLDashboardService.getDashboardData()
    console.log('Dashboard response:', response)
    
    if (response?.metrics) {
      const backendMetrics = response.metrics as Record<string, string>
      console.log('Backend metrics:', backendMetrics)
      
      metrics.value = [
        {
          title: 'Active Workforce',
          value: backendMetrics['Average Demand'] || 'N/A',
          change: '0%',
          rating: 'medium',
          icon: 'pi pi-users',
        },
        {
          title: 'Forecast Accuracy',
          value: backendMetrics['Accuracy'] || 'N/A',
          change: '0%',
          rating: 'medium',
          icon: 'pi pi-chart-line',
        },
        { 
          title: 'Shift Utilization', 
          value: backendMetrics['Total Predictions'] || 'N/A', 
          change: '0%', 
          rating: 'medium', 
          icon: 'pi pi-clock' 
        },
        {
          title: 'Total Predictions',
          value: backendMetrics['Total Predictions'] || 'N/A',
          change: '0%',
          rating: 'medium',
          icon: 'pi pi-list',
        },
        {
          title: 'Average Demand',
          value: backendMetrics['Average Demand'] || 'N/A',
          change: '0%',
          rating: 'medium',
          icon: 'pi pi-box',
        },
        {
          title: 'Model Status',
          value: backendMetrics['Status'] || 'N/A',
          change: '',
          rating: 'medium',
          icon: 'pi pi-cog',
        },
        {
          title: 'Model Name',
          value: backendMetrics['Model Name'] || 'Not Trained',
          change: '',
          rating: 'medium',
          icon: 'pi pi-database',
        },
        {
          title: 'R² Score',
          value: backendMetrics['R² Score'] || 'N/A',
          change: '0%',
          rating: 'medium',
          icon: 'pi pi-chart-bar',
        },
        { 
          title: 'RMSE', 
          value: backendMetrics['RMSE'] || 'N/A', 
          change: '', 
          rating: 'medium', 
          icon: 'pi pi-chart-bar' 
        },
      ]
    } else {
      console.log('No metrics in response')
    }

    // Update chart data from backend
    if (response?.charts) {
      const charts = response.charts as Record<string, any>
      
      // Update line chart
      if (charts.lineChart?.labels && charts.lineChart?.historical && charts.lineChart?.predicted) {
        chartData.value = setChartData(
          charts.lineChart.labels,
          charts.lineChart.historical,
          charts.lineChart.predicted
        )
      }
      
      // Update bar chart
      if (charts.barChart?.labels && charts.barChart?.performance && charts.barChart?.target) {
        barChartData.value = setBarChartData(
          charts.barChart.labels,
          charts.barChart.performance,
          charts.barChart.target
        )
      }
    }
  } catch (error) {
    console.error('Failed to fetch dashboard metrics:', error)
    // Keep default values on error
  } finally {
    loading.value = false
  }
}

/* ---------------- CHART ---------------- */
const chartData = ref()
const chartOptions = ref()
const barChartData = ref()
const barChartOptions = ref()

onMounted(() => {
  fetchDashboardMetrics()
  
  chartOptions.value = setChartOptions()
  barChartOptions.value = setBarChartOptions()

  // Set up real-time refresh every 30 seconds
  refreshInterval = window.setInterval(() => {
    fetchDashboardMetrics()
  }, 30000)
})

onUnmounted(() => {
  if (refreshInterval) {
    clearInterval(refreshInterval)
    refreshInterval = null
  }
})

const setChartData = (labels: string[] = ['January', 'February', 'March', 'April', 'May', 'June', 'July'], historical: number[] = [65, 59, 80, 81, 56, 55, 10], predicted: number[] = [28, 48, 40, 19, 86, 27, 90]) => {
  const documentStyle = getComputedStyle(document.documentElement)

  return {
    labels: labels,
    datasets: [
      {
        label: 'Historical Demand',
        fill: false,
        borderColor: documentStyle.getPropertyValue('--p-cyan-500'),
        tension: 0.4,
        data: historical,
      },
      {
        label: 'Predicted Demand',
        fill: false,
        borderColor: documentStyle.getPropertyValue('--p-gray-500'),
        tension: 0.4,
        data: predicted,
      },
    ],
  }
}

const setChartOptions = () => {
  const documentStyle = getComputedStyle(document.documentElement)
  const textColor = documentStyle.getPropertyValue('--p-text-color')
  const textColorSecondary = documentStyle.getPropertyValue('--p-text-muted-color')
  const surfaceBorder = documentStyle.getPropertyValue('--p-content-border-color')

  return {
    maintainAspectRatio: false,
    plugins: {
      legend: {
        labels: { color: textColor },
      },
    },
    scales: {
      x: {
        ticks: { color: textColorSecondary },
        grid: { color: surfaceBorder },
      },
      y: {
        ticks: { color: textColorSecondary },
        grid: { color: surfaceBorder },
      },
    },
  }
}

const setBarChartData = (labels: string[] = ['January', 'February', 'March', 'April', 'May', 'June', 'July'], performance: number[] = [65, 59, 80, 81, 56, 55, 40], target: number[] = [28, 48, 40, 19, 86, 27, 90]) => {
  const documentStyle = getComputedStyle(document.documentElement)

  return {
    labels: labels,
    datasets: [
      {
        label: 'Department Performance',
        backgroundColor: documentStyle.getPropertyValue('--p-cyan-500'),
        borderColor: documentStyle.getPropertyValue('--p-cyan-500'),
        data: performance,
      },
      {
        label: 'Target Performance',
        backgroundColor: documentStyle.getPropertyValue('--p-gray-500'),
        borderColor: documentStyle.getPropertyValue('--p-gray-500'),
        data: target,
      },
    ],
  }
}

const setBarChartOptions = () => {
  const documentStyle = getComputedStyle(document.documentElement)
  const textColor = documentStyle.getPropertyValue('--p-text-color')
  const textColorSecondary = documentStyle.getPropertyValue('--p-text-muted-color')
  const surfaceBorder = documentStyle.getPropertyValue('--p-content-border-color')

  return {
    indexAxis: 'y',
    maintainAspectRatio: false,
    aspectRatio: 0.8,
    plugins: {
      legend: {
        labels: {
          color: textColor,
        },
      },
    },
    scales: {
      x: {
        ticks: {
          color: textColorSecondary,
          font: {
            weight: 500,
          },
        },
        grid: {
          display: false,
          drawBorder: false,
        },
      },
      y: {
        ticks: {
          color: textColorSecondary,
        },
        grid: {
          color: surfaceBorder,
          drawBorder: false,
        },
      },
    },
  }
}
</script>

<style scoped src="./dashboardComponent.css"></style>
