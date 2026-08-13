<template>
  <div class="dashboard">
    <h1>{{ lbl.operationsCommandCenter }}</h1>
    <p>{{ lbl.operationsCommandCenterDesc }}</p>
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

      <div class="microserviceCard">
        <div class="cardHeader">
          <div>
            <h3>Microservice Health</h3>
            <p>7/8 Healthy</p>
          </div>

          <div class="healthBadge">
            <i class="pi pi-check-circle"></i>
            Healthy
          </div>
        </div>

        <div v-for="service in microservices" :key="service.name" class="serviceRow">
          <div class="serviceName">
            <span class="statusDot" :class="service.status"></span>

            <div>
              <h4>{{ service.name }}</h4>
              <small>{{ service.instances }} instances</small>
            </div>
          </div>

          <div class="resourceInfo">
            <div class="metric">
              <span>CPU</span>
              <strong>{{ service.cpu }}%</strong>
            </div>

            <div class="metric">
              <span>Memory</span>
              <strong>{{ service.memory }}%</strong>
            </div>
          </div>
        </div>
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
import { ref, onMounted } from 'vue'
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
import CLDashboardService from "./dashboardService";
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
const getDashoboardData = CLDashboardService.getDashboardData()
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
    value: '94.2%',
    change: '1.8%',
    rating: 'high',
    icon: 'pi pi-users',
  },
  {
    title: 'Forecast Accuracy',
    value: '87.6%',
    change: '0.4%',
    rating: 'medium',
    icon: 'pi pi-chart-line',
  },
  { title: 'Shift Utilization', value: '6', change: '12%', rating: 'low', icon: 'pi pi-clock' },
  {
    title: 'Open Alerts',
    value: '34.2K',
    change: '8.5%',
    rating: 'critical',
    icon: 'pi pi-exclamation-triangle',
  },
  {
    title: 'Orders Processed',
    value: '89.1%',
    change: '2.1%',
    rating: 'high',
    icon: 'pi pi-box',
  },
  {
    title: 'Avg Productivity',
    value: '78.4%',
    change: '0%',
    rating: 'medium',
    icon: 'pi pi-arrow-up',
  },
  {
    title: 'Capacity Load',
    value: '99.97%',
    change: '0.02%',
    rating: 'high',
    icon: 'pi pi-chart-bar',
  },
  { title: 'API Uptime', value: '100%', change: '', rating: 'perfect', icon: 'pi pi-wifi' },
])
const microservices = ref([
  {
    name: 'forecast-service',
    instances: 3,
    cpu: 34,
    memory: 62,
    status: 'healthy',
  },
  {
    name: 'shift-optimizer',
    instances: 2,
    cpu: 28,
    memory: 55,
    status: 'healthy',
  },
  {
    name: 'employee-service',
    instances: 3,
    cpu: 22,
    memory: 48,
    status: 'healthy',
  },
  {
    name: 'analytics-engine',
    instances: 2,
    cpu: 67,
    memory: 78,
    status: 'warning',
  },
  {
    name: 'auth-service',
    instances: 2,
    cpu: 12,
    memory: 35,
    status: 'healthy',
  },
  {
    name: 'notification-service',
    instances: 1,
    cpu: 18,
    memory: 42,
    status: 'healthy',
  },
  {
    name: 'ml-pipeline',
    instances: 2,
    cpu: 72,
    memory: 81,
    status: 'critical',
  },
  {
    name: 'api-gateway',
    instances: 3,
    cpu: 15,
    memory: 38,
    status: 'healthy',
  },
])

/* ---------------- CHART ---------------- */
const chartData = ref()
const chartOptions = ref()
const barChartData = ref()
const barChartOptions = ref()

onMounted(() => {

  
  chartData.value = setChartData()
  chartOptions.value = setChartOptions()
  barChartData.value = setBarChartData()
  barChartOptions.value = setBarChartOptions()
})

const setChartData = () => {
  const documentStyle = getComputedStyle(document.documentElement)

  return {
    labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
    datasets: [
      {
        label: 'Dataset 1',
        fill: false,
        borderColor: documentStyle.getPropertyValue('--p-cyan-500'),
        tension: 0.4,
        data: [65, 59, 80, 81, 56, 55, 10],
      },
      {
        label: 'Dataset 2',
        fill: false,
        borderColor: documentStyle.getPropertyValue('--p-gray-500'),
        tension: 0.4,
        data: [28, 48, 40, 19, 86, 27, 90],
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

const setBarChartData = () => {
  const documentStyle = getComputedStyle(document.documentElement)

  return {
    labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
    datasets: [
      {
        label: 'Department Performance',
        backgroundColor: documentStyle.getPropertyValue('--p-cyan-500'),
        borderColor: documentStyle.getPropertyValue('--p-cyan-500'),
        data: [65, 59, 80, 81, 56, 55, 40],
      },
      {
        label: 'Target Performance',
        backgroundColor: documentStyle.getPropertyValue('--p-gray-500'),
        borderColor: documentStyle.getPropertyValue('--p-gray-500'),
        data: [28, 48, 40, 19, 86, 27, 90],
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
