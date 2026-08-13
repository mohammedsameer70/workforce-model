<template>
  <div class="col-12 p-0 m-0">
    <div class="dashboard">
      <h1>{{ lbl.operationAnalytics }}</h1>
      <p>{{ lbl.operationAnalyticsDesc }}</p>
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
        <div>Loading analytics data...</div>
      </div>
    </div>

    <div class="col-12">
      <SelectButton
        v-model="r_selectTab"
        :options="options"
        optionLabel="label"
        optionValue="value"
        fluid
      />
    </div>
    <Panel v-if="r_selectTab === 0" :header="lbl.productuctTitle" class="mb-4">
      <div class="departmentSection">
        <div class="chartCard">
          <template v-if="loading">
            <div class="page-loading-overlay">
              <div class="page-loading-panel">
                <div class="page-loading-spinner"></div>
                <div>Loading analytics charts...</div>
              </div>
            </div>
          </template>

          <Chart v-else type="line" :data="chartData" :options="chartOptions" style="height: 420px" />
        </div>
      </div>
    </Panel>

    <!-- WEEKLY FORECAST -->
    <Panel v-if="r_selectTab === 1" header="Department Performance" class="mb-4">
      <div class="departmentSection">
        <div class="chartCard">
          <Chart
            type="line"
            :data="throughputChartData"
            :options="throughputChartOptions"
            style="height: 420px"
          />
        </div>
      </div> </Panel
    ><Panel v-if="r_selectTab === 2" header="Department Performance" class="mb-4">
      <div class="departmentSection">
        <div class="chartCard darkChart">
          <div class="chartHeader">
            <h3>Workforce by Department</h3>
            <p>Current headcount distribution</p>
          </div>

          <Chart
            class="flex justify-center"
            type="doughnut"
            :data="departmentChartData"
            :options="departmentChartOptions"
            style="height: 320px; width: 100%"
          />
        </div>
        <div class="chartCard darkChart">
          <div class="chartHeader">
            <h3>Weekly Demand Comparison</h3>
            <p>Demand vs staffed vs optimal</p>
          </div>

          <Chart
            type="bar"
            :data="weeklyChartData"
            :options="weeklyChartOptions"
            style="height: 320px"
          />
        </div>
      </div>
    </Panel>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Chart from 'primevue/chart'
import Panel from 'primevue/panel'
import SelectButton from 'primevue/selectbutton'
import { lbl } from '@/assets/constants/labels'
import AnalyticsService from './analyticsService'
import type {
  AnalyticsMetricDTO,
  TimeSeriesPointDTO,
  DepartmentDistributionDTO,
} from './analyticsService'
import {
  Chart as ChartJS,
  LineElement,
  BarElement,
  ArcElement,
  BarController,
  CategoryScale,
  LinearScale,
  PointElement,
  Tooltip,
  Legend,
} from 'chart.js'

ChartJS.register(
  LineElement,
  BarElement,
  ArcElement,
  BarController,
  CategoryScale,
  LinearScale,
  PointElement,
  Tooltip,
  Legend,
)

const r_selectTab = ref(0)
const options = [
  {
    label: lbl.performance,
    value: 0,
  },
  {
    label: lbl.throughout,
    value: 1,
  },
  {
    label: lbl.distribution,
    value: 2,
  },
]
const metrics = ref<AnalyticsMetricDTO[]>([])
const chartData = ref<any>({ labels: [], datasets: [] })
const chartOptions = ref<any>({})
const throughputChartData = ref<any>({ labels: [], datasets: [] })
const throughputChartOptions = ref<any>({})
const departmentChartData = ref<any>({ labels: [], datasets: [] })
const departmentChartOptions = ref<any>({})
const weeklyChartData = ref<any>({ labels: [], datasets: [] })
const weeklyChartOptions = ref<any>({})
const loading = ref(false)
const error = ref('')

const loadAnalyticsData = async () => {
  loading.value = true
  error.value = ''

  try {
    const [metricResponse, hourlyResponse, forecastResponse, distributionResponse, weeklyResponse] =
      await Promise.all([
        AnalyticsService.getMetrics(),
        AnalyticsService.getHourlyThroughput(),
        AnalyticsService.getDemandForecast(),
        AnalyticsService.getDepartmentDistribution(),
        AnalyticsService.getWeeklyComparison(),
      ])

    metrics.value = metricResponse
    chartData.value = buildLineChart(hourlyResponse, 'Throughput')
    throughputChartData.value = buildLineChart(forecastResponse, 'Forecast')
    departmentChartData.value = buildDoughnutChart(distributionResponse)
    weeklyChartData.value = buildBarChart(weeklyResponse)
  } catch (err) {
    console.error('Failed to load analytics data', err)
    error.value = 'Unable to load analytics data.'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadAnalyticsData()
  chartOptions.value = buildLineOptions()
  throughputChartOptions.value = buildLineOptions()
  departmentChartOptions.value = buildDoughnutOptions()
  weeklyChartOptions.value = buildBarOptions()
})

const buildLineChart = (items: TimeSeriesPointDTO[], label: string) => {
  return {
    labels: items.map((item) => item.label),
    datasets: [
      {
        label,
        data: items.map((item) => item.value),
        borderColor: '#0EA5E9',
        backgroundColor: 'rgba(14,165,233,0.15)',
        fill: true,
        tension: 0.4,
      },
    ],
  }
}

const buildDoughnutChart = (items: DepartmentDistributionDTO[]) => {
  return {
    labels: items.map((item) => item.department),
    datasets: [
      {
        data: items.map((item) => item.value),
        backgroundColor: ['#0EA5E9', '#34D399', '#A855F7', '#F59E0B', '#F43F5E', '#2563EB'],
        hoverOffset: 8,
      },
    ],
  }
}

const buildBarChart = (items: TimeSeriesPointDTO[]) => {
  return {
    labels: items.map((item) => item.label),
    datasets: [
      {
        label: 'Weekly Comparison',
        data: items.map((item) => item.value),
        backgroundColor: '#0EA5E9',
        borderRadius: 6,
      },
    ],
  }
}

const buildLineOptions = () => ({
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'bottom',
      labels: { color: '#94A3B8' },
    },
  },
  scales: {
    x: { ticks: { color: '#64748B' }, grid: { color: 'rgba(148,163,184,0.08)' } },
    y: { beginAtZero: true, ticks: { color: '#64748B' }, grid: { color: 'rgba(148,163,184,0.08)' } },
  },
})

const buildDoughnutOptions = () => ({
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'bottom',
      labels: { color: '#94A3B8', usePointStyle: true, pointStyle: 'circle' },
    },
  },
})

const buildBarOptions = () => ({
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'bottom',
      labels: { color: '#94A3B8' },
    },
  },
  scales: {
    x: { ticks: { color: '#64748B' }, grid: { color: 'rgba(148,163,184,0.08)' } },
    y: { beginAtZero: true, ticks: { color: '#64748B' }, grid: { color: 'rgba(148,163,184,0.08)' } },
  },
})
</script>
