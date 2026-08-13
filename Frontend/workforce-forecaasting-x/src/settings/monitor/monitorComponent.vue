<template>
  <div class="infraContainer">
    <!-- HEADER -->
    <div class="dashboardHeader">
      <h1>Infrastructure Monitoring</h1>
      <p>Real-time system health and latency tracking</p>
    </div>
    <div class="cardsInfo">
      <div class="card" v-for="metric in metrics" :key="metric.title">
        <div class="iconBox">
          <i :class="metric.icon" class="icon"></i>
        </div>

        <h3>{{ metric.title }}</h3>
        <p>{{ metric.value }}</p>
      </div>
    </div>

    <div v-if="loading" class="page-loading-overlay">
      <div class="page-loading-panel">
        <div class="page-loading-spinner"></div>
        <div>Loading monitoring charts...</div>
      </div>
    </div>

    <!-- INFRASTRUCTURE METRICS -->
    <Panel class="dashboardPanel">
      <template #header>
        <div class="panelHeader">
          <div>
            <h3>Infrastructure Metrics</h3>
            <p>CPU, Memory and Network utilization over time</p>
          </div>
        </div>
      </template>

      <template v-if="loading">
        <div class="page-loading-overlay">
          <div class="page-loading-panel">
            <div class="page-loading-spinner"></div>
            <div>Loading infrastructure charts...</div>
          </div>
        </div>
      </template>

      <Chart
        v-else
        type="line"
        :data="infrastructureData"
        :options="lineChartOptions"
        style="height: 380px"
      />
    </Panel>

    <!-- API LATENCY -->
    <Panel class="dashboardPanel">
      <template #header>
        <div class="panelHeader">
          <div>
            <h3>API Response Latency</h3>
            <p>Endpoint response times in milliseconds</p>
          </div>
        </div>
      </template>

      <Chart type="line" :data="latencyData" :options="latencyOptions" style="height: 380px" />
    </Panel>

    
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Panel from 'primevue/panel'
import Chart from 'primevue/chart'
import MonitorService from './monitorService'
import type {
  InfrastructureMetricDTO,
  LatencyPointDTO,
} from './monitorService'

const infrastructureData = ref({ labels: [] as string[], datasets: [] as any[] })
const latencyData = ref({ labels: [] as string[], datasets: [] as any[] })
const lineChartOptions = ref()
const latencyOptions = ref()
const metrics = ref([
  {
    title: '99.97%',
    value: 'System Uptime',
    icon: 'pi pi-chart-scatter',
  },
  {
    title: '34%',
    value: 'Avg CPU Usage',
    icon: 'pi pi-microchip',
  },
  {
    title: '58%',
    value: 'Memory Usage',
    icon: 'pi pi-database',
  },
  {
    title: '42ms',
    value: 'Avg Latency',
    icon: 'pi pi-wifi',
  },
])

const loading = ref(false)
const error = ref('')

const loadMonitorData = async () => {
  loading.value = true
  error.value = ''

  try {
    const [infrastructure, latency] = await Promise.all([
      MonitorService.getInfrastructureMetrics(),
      MonitorService.getApiLatency(),
    ])

    infrastructureData.value = buildInfrastructureData(infrastructure)
    latencyData.value = buildLatencyData(latency)
  } catch (err) {
    console.error('Failed to load monitor data', err)
    error.value = 'Unable to load monitoring data.'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadMonitorData()
  lineChartOptions.value = buildLineOptions()
  latencyOptions.value = buildLineOptions()
})

const buildInfrastructureData = (_items: InfrastructureMetricDTO[]) => {
  return {
    labels: [
      '30m',
      '29m',
      '28m',
      '27m',
      '26m',
      '25m',
      '24m',
      '23m',
      '22m',
      '21m',
      '20m',
      '19m',
      '18m',
      '17m',
      '16m',
      '15m',
      '14m',
      '13m',
      '12m',
      '11m',
      '10m',
      '9m',
      '8m',
      '7m',
      '6m',
      '5m',
      '4m',
      '3m',
      '2m',
      '1m',
      '0m',
    ],

    datasets: [
      {
        label: 'CPU %',
        borderColor: '#0ea5e9',
        backgroundColor: 'rgba(14,165,233,.12)',
        fill: true,
        tension: 0.4,
        data: [
          55, 58, 52, 60, 62, 57, 63, 66, 59, 61, 65, 67, 64, 70, 66, 72, 68, 73, 69, 71, 74, 70,
          72, 68, 66, 64, 63, 67, 69, 70, 72,
        ],
      },

      {
        label: 'Memory %',
        borderColor: '#a855f7',
        backgroundColor: 'rgba(168,85,247,.12)',
        fill: true,
        tension: 0.4,
        data: [
          45, 47, 50, 52, 49, 51, 54, 56, 53, 58, 60, 59, 61, 62, 64, 63, 66, 68, 67, 65, 69, 70,
          68, 72, 74, 73, 71, 70, 69, 72, 74,
        ],
      },

      {
        label: 'Network %',
        borderColor: '#22c55e',
        borderDash: [6, 6],
        tension: 0.4,
        data: [
          30, 28, 35, 32, 36, 34, 38, 35, 37, 39, 42, 40, 41, 43, 45, 47, 44, 46, 48, 50, 47, 49,
          51, 48, 46, 44, 43, 45, 47, 49, 52,
        ],
      },
    ],
  }
}
const buildLatencyData = (_items: LatencyPointDTO[]) => {
  return {
    labels: [
      '29s ago',
      '28s ago',
      '27s ago',
      '26s ago',
      '25s ago',
      '24s ago',
      '23s ago',
      '22s ago',
      '21s ago',
      '20s ago',
      '19s ago',
      '18s ago',
      '17s ago',
      '16s ago',
      '15s ago',
    ],

    datasets: [
      {
        label: 'predict',
        borderColor: '#0ea5e9',
        tension: 0.4,
        data: [85, 82, 88, 92, 87, 90, 95, 91, 89, 94, 92, 90, 96, 93, 91],
      },

      {
        label: 'optimize',
        borderColor: '#22c55e',
        tension: 0.4,
        data: [65, 68, 70, 72, 74, 69, 75, 73, 77, 79, 76, 74, 80, 82, 78],
      },

      {
        label: 'employees',
        borderColor: '#a855f7',
        tension: 0.4,
        data: [45, 50, 48, 52, 49, 53, 55, 57, 54, 58, 56, 59, 60, 57, 55],
      },

      {
        label: 'kpi',
        borderColor: '#f59e0b',
        tension: 0.4,
        data: [35, 38, 40, 39, 42, 45, 43, 47, 46, 48, 50, 49, 52, 51, 50],
      },
    ],
  }
}

const buildLineOptions = () => {
  return {
    responsive: true,
    maintainAspectRatio: false,

    interaction: {
      mode: 'index',
      intersect: false,
    },

    plugins: {
      legend: {
        position: 'bottom',
        labels: {
          color: '#94a3b8',
          usePointStyle: true,
          pointStyle: 'circle',
        },
      },
    },

    scales: {
      x: {
        ticks: {
          color: '#64748b',
        },
        grid: {
          color: 'rgba(148,163,184,0.08)',
        },
      },

      y: {
        ticks: {
          color: '#64748b',
        },
        grid: {
          color: 'rgba(148,163,184,0.08)',
        },
      },
    },
  }
}
</script>
<style scoped src="./monitorComponent.css"></style>
