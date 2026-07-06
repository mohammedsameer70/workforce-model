<template>
  <div class="benchmarkContainer">
    <!-- HEADER -->
    <div class="pageHeader">
      <h1>Benchmarks & Experimentation</h1>
      <p>Performance testing, load simulation, and A/B experiments</p>
    </div>

    <!-- KPI CARDS -->
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

      <Chart
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

const latencyChartData = ref()
const versionChartData = ref()

const lineChartOptions = ref()
const barChartOptions = ref()

const metrics = ref([
  {
    value: '47',
    title: 'Total Experiments',
    trend: '+8.2%',
    type: 'positive',
    icon: 'pi pi-flask',
  },
  {
    value: '62ms',
    title: 'Avg P95 Latency',
    trend: '-15.3%',
    type: 'negative',
    icon: 'pi pi-clock',
  },
  {
    value: '4,850/s',
    title: 'Max Throughput',
    trend: '+12.5%',
    type: 'positive',
    icon: 'pi pi-chart-line',
  },
  {
    value: '67%',
    title: 'CPU Under Load',
    trend: '+3.2%',
    type: 'warning',
    icon: 'pi pi-cog',
  },
])

const experiments = ref([
  {
    id: 'EXP-001',
    title: 'LSTM vs XGBoost Accuracy',
    result: 'LSTM +2.4% accuracy',
    status: 'completed',
    date: '2024-01-15',
    duration: '4h 23m',
  },
  {
    id: 'EXP-002',
    title: 'Load Test: 5000 Concurrent',
    result: 'P95 < 200ms achieved',
    status: 'completed',
    date: '2024-01-14',
    duration: '1h 45m',
  },
  {
    id: 'EXP-003',
    title: 'Spring Boot vs Node.js API',
    result: 'In Progress',
    status: 'running',
    date: '2024-01-15',
    duration: '2h 10m',
  },
  {
    id: 'EXP-004',
    title: 'DB Query Optimization',
    result: '35% latency reduction',
    status: 'completed',
    date: '2024-01-13',
    duration: '45m',
  },
  {
    id: 'EXP-005',
    title: 'Horizontal Scaling Test',
    result: 'Pending',
    status: 'queued',
    date: '2024-01-12',
    duration: '--',
  },
])

onMounted(() => {
  latencyChartData.value = buildLatencyChart()
  versionChartData.value = buildVersionChart()

  lineChartOptions.value = buildLineOptions()
  barChartOptions.value = buildBarOptions()
})

const buildLatencyChart = () => {
  return {
    labels: ['50', '100', '200', '500', '1000', '2000', '3000', '5000'],

    datasets: [
      {
        label: 'P50 (ms)',
        borderColor: '#00B2FF',
        backgroundColor: '#00B2FF',
        tension: 0.4,
        data: [20, 20, 20, 28, 35, 40, 48, 70],
      },

      {
        label: 'P95 (ms)',
        borderColor: '#F59E0B',
        backgroundColor: '#F59E0B',
        tension: 0.4,
        data: [45, 50, 35, 65, 52, 78, 105, 150],
      },

      {
        label: 'P99 (ms)',
        borderColor: '#FF2E88',
        backgroundColor: '#FF2E88',
        tension: 0.4,
        data: [95, 78, 108, 80, 130, 128, 170, 250],
      },
    ],
  }
}
const buildVersionChart = () => {
  return {
    labels: ['v1.0', 'v1.5', 'v2.0', 'v2.5', 'v3.0', 'v3.2'],

    datasets: [
      {
        label: 'Throughput (req/s)',
        backgroundColor: '#00B2FF',
        borderRadius: 8,
        data: [3200, 4700, 2500, 4200, 4800, 4900],
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
          color: '#64748B',
          usePointStyle: true,
          pointStyle: 'circle',
        },
      },
    },

    scales: {
      x: {
        title: {
          display: true,
          text: 'Concurrent Users',
          color: '#64748B',
        },
        ticks: {
          color: '#64748B',
        },
        grid: {
          color: 'rgba(148,163,184,.08)',
        },
      },

      y: {
        title: {
          display: true,
          text: 'Latency (ms)',
          color: '#64748B',
        },
        ticks: {
          color: '#64748B',
        },
        grid: {
          color: 'rgba(148,163,184,.08)',
        },
      },
    },
  }
}

const buildBarOptions = () => {
  return {
    responsive: true,
    maintainAspectRatio: false,

    plugins: {
      legend: {
        position: 'bottom',
        labels: {
          color: '#64748B',
          usePointStyle: true,
          pointStyle: 'circle',
        },
      },
    },

    scales: {
      x: {
        ticks: {
          color: '#64748B',
        },
        grid: {
          display: false,
        },
      },

      y: {
        ticks: {
          color: '#64748B',
        },
        grid: {
          color: 'rgba(148,163,184,.08)',
        },
      },
    },
  }
}
</script>

<style scoped src="./benchmarkComponent.css"></style>
