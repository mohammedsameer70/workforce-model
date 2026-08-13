<template>
  <div class="benchmarkContainer">
    <!-- HEADER -->
    <div class="pageHeader">
      <h1>Benchmarks & Experimentation</h1>
      <p>Performance testing, load simulation, and A/B experiments</p>
    </div>

    <!-- KPI CARDS -->
    <div v-if="loading" class="page-loading-overlay">
      <div class="page-loading-panel">
        <div class="page-loading-spinner"></div>
        <div>Loading benchmark data...</div>
      </div>
    </div>

    <div class="metricsGrid">
      <div class="metricCard" v-for="metric in metrics" :key="metric.title">
        <div class="cardHeader">
          <div class="iconBox">
            <i :class="metric.icon"></i>
          </div>

          <span class="trendBadge" :class="metric.type">
            {{ metric.trend }}
          </span>
        </div>

        <h2>{{ metric.value }}</h2>
        <p>{{ metric.title }}</p>

        <div class="cardGlow"></div>
      </div>
    </div>

    <!-- API LATENCY -->
    <Panel class="dashboardPanel">
      <template #header>
        <div class="panelHeader">
          <div>
            <h3>API Latency Under Load</h3>
            <p>P50, P95, P99 response times by concurrent user count</p>
          </div>
        </div>
      </template>

      <div v-if="loading" class="page-loading-overlay">
        <div class="page-loading-panel">
          <div class="page-loading-spinner"></div>
          <div>Loading benchmark charts...</div>
        </div>
      </div>

      <Chart
        v-else
        type="line"
        :data="latencyChartData"
        :options="lineChartOptions"
        style="height: 360px"
      />
    </Panel>

    <!-- BOTTOM GRID -->
    <div class="bottomGrid">
      <!-- VERSION HISTORY -->
      <Panel class="dashboardPanel">
        <template #header>
          <div class="panelHeader">
            <div>
              <h3>Version Performance History</h3>
              <p>Throughput and accuracy across releases</p>
            </div>
          </div>
        </template>

        <Chart
          type="bar"
          :data="versionChartData"
          :options="barChartOptions"
          style="height: 320px"
        />
      </Panel>

      <!-- EXPERIMENT LOG -->
      <Panel class="dashboardPanel">
        <template #header>
          <div class="panelHeader">
            <div>
              <h3>Experiment Log</h3>
              <p>Recent performance tests and A/B experiments</p>
            </div>
          </div>
        </template>

        <div class="experimentItem" v-for="exp in experiments" :key="exp.id">
          <div class="experimentTop">
            <span class="experimentId">
              {{ exp.id }}
            </span>

            <span class="statusBadge" :class="exp.status">
              {{ exp.status }}
            </span>
          </div>

          <h4>{{ exp.title }}</h4>

          <p class="experimentResult">
            {{ exp.result }}
          </p>

          <div class="experimentFooter">
            <span>{{ exp.date }}</span>
            <span>{{ exp.duration }}</span>
          </div>
        </div>
      </Panel>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Panel from 'primevue/panel'
import Chart from 'primevue/chart'
import BenchmarkService from './benchmarkService'
import type {
  BenchmarkMetricDTO,
  LatencyPointDTO,
  VersionHistoryDTO,
  ExperimentDTO,
} from './benchmarkService'

const metrics = ref<BenchmarkMetricDTO[]>([])
const latencyChartData = ref<any>({ labels: [], datasets: [] })
const versionChartData = ref<any>({ labels: [], datasets: [] })
const lineChartOptions = ref<any>({})
const barChartOptions = ref<any>({})
const experiments = ref<ExperimentDTO[]>([])
const loading = ref(false)
const error = ref('')

const loadBenchmarkData = async () => {
  loading.value = true
  error.value = ''

  try {
    const [metricResponse, latencyResponse, versionResponse, experimentResponse] =
      await Promise.all([
        BenchmarkService.getMetrics(),
        BenchmarkService.getLatencySeries(),
        BenchmarkService.getVersionHistory(),
        BenchmarkService.getExperiments(),
      ])

    metrics.value = metricResponse
    latencyChartData.value = buildLatencyChart(latencyResponse)
    versionChartData.value = buildVersionChart(versionResponse)
    experiments.value = experimentResponse
  } catch (err) {
    console.error('Failed to load benchmark data', err)
    error.value = 'Unable to load benchmark data.'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadBenchmarkData()
  lineChartOptions.value = buildLineOptions()
  barChartOptions.value = buildBarOptions()
})

const buildLatencyChart = (items: LatencyPointDTO[]) => ({
  labels: items.map((item) => item.label),
  datasets: [
    {
      label: 'P50 (ms)',
      borderColor: '#00B2FF',
      backgroundColor: '#00B2FF',
      tension: 0.4,
      data: items.map((item) => item.value),
    },
  ],
})

const buildVersionChart = (items: VersionHistoryDTO[]) => ({
  labels: items.map((item) => item.label),
  datasets: [
    {
      label: 'Throughput (req/s)',
      backgroundColor: '#00B2FF',
      borderRadius: 8,
      data: items.map((item) => item.throughput),
    },
  ],
})

const buildLineOptions = () => ({
  responsive: true,
  maintainAspectRatio: false,
  interaction: {
    mode: 'index',
    intersect: false,
  },
  plugins: {
    legend: {
      position: 'bottom',
      labels: { color: '#64748B', usePointStyle: true, pointStyle: 'circle' },
    },
  },
  scales: {
    x: { ticks: { color: '#64748B' }, grid: { color: 'rgba(148,163,184,.08)' } },
    y: { ticks: { color: '#64748B' }, grid: { color: 'rgba(148,163,184,.08)' } },
  },
})

const buildBarOptions = () => ({
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'bottom',
      labels: { color: '#64748B', usePointStyle: true, pointStyle: 'circle' },
    },
  },
  scales: {
    x: { ticks: { color: '#64748B' }, grid: { display: false } },
    y: { ticks: { color: '#64748B' }, grid: { color: 'rgba(148,163,184,.08)' } },
  },
})
</script>

<style scoped src="./benchmarkComponent.css"></style>
