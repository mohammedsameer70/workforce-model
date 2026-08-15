<template>
  <div class="dashboard">
    <h1>{{ lbl.workforce }}</h1>
    <p>{{ lbl.workforceDesc }}</p>
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

  <div class="forecasting-tabbar">
    <SelectButton
      v-model="r_selectTab"
      :options="options"
      optionLabel="label"
      optionValue="value"
      fluid
      :disabled="!canToggleTabs"
      :unselectable="false"
    >
      <template #option="slotProps">
        <div class="tab-option">
          <i :class="slotProps.option.icon"></i>
          <span>{{ slotProps.option.label }}</span>
        </div>
      </template>
    </SelectButton>
    <div v-if="!canToggleTabs" class="forecasting-tabbar-help">
      <small>
        Forecast tabs are disabled until the AI model has finished training and data is loaded.
      </small>
    </div>
  </div>

  <div v-if="loading" class="page-loading-overlay">
    <div class="page-loading-panel">
      <div class="page-loading-spinner"></div>
      <div>Loading forecasting charts and data...</div>
    </div>
  </div>

  <!-- 24 HOURS FORECAST -->
  <Panel v-show="r_selectTab === 0" :header="lbl.HourlyDemandPrediction" class="mb-4">
    <div class="departmentSection">
      <div class="chartCard">
        <Chart
          type="line"
          style="height: 100%"
          :data="chartData"
          :options="chartOptions"
          class="h-[30rem]"
        />
      </div>
    </div>
  </Panel>

  <!-- WEEKLY FORECAST -->
  <Panel v-show="r_selectTab === 1" header="Department Performance" class="mb-4">
    <div class="departmentSection">
      <div class="chartCard">
        <Chart
          type="bar"
          :data="barChartData"
          :options="barChartOptions"
          style="height: 100%"
          class="h-[30rem]"
        />
      </div>
    </div>
  </Panel>

  <!-- STAFFING TABLE -->
  <div class="flex flex-row w-full">
    <Panel header="Department Staffing Heatmap" class="mb-4 w-full">
      <p class="tableSubTitle">Current shift allocation by department</p>

      <DataTable :value="staffingData" stripedRows showGridlines responsiveLayout="scroll">
        <Column field="department" header="Department" />

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
    <Panel header="Department Staffing Heatmap" class="mb-4 w-full">
      <Chart
        type="radar"
        :data="radarChartData"
        style="height: 30%; width: 100%"
        :options="radarChartOptions"
        class="w-full md:w-[30rem]"
      />
    </Panel>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed, watch } from 'vue'

import Chart from 'primevue/chart'
import Panel from 'primevue/panel'
import SelectButton from 'primevue/selectbutton'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'

import { lbl } from '@/assets/constants/labels'
import { aiModelReady } from '@/state/aiModelGate'
import ForecastingService from './forecastingService'
import type {
  ForecastMetricDTO,
  ForecastTrendDTO,
  WeeklyForecastDTO,
  StaffingHeatmapDTO,
} from './forecastingService'

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

const r_selectTab = ref(0)

const options = [
  {
    label: lbl.twoFourhours,
    value: 0,
    icon: 'pi pi-clock'
  },
  {
    label: lbl.weeklyForeCast,
    value: 1,
    icon: 'pi pi-calendar'
  },
]

const metrics = ref<ForecastMetricDTO[]>([])
const chartData = ref<any>({ labels: [], datasets: [] })
const chartOptions = ref<any>({})
const barChartData = ref<any>({ labels: [], datasets: [] })
const barChartOptions = ref<any>({})
const radarChartData = ref<any>({ labels: [], datasets: [] })
const radarChartOptions = ref<any>({})
const staffingData = ref<StaffingHeatmapDTO[]>([])
const loading = ref(true)
const error = ref('')
let refreshInterval: number | null = null

const canToggleTabs = computed(() => !loading.value && aiModelReady.value)
watch(r_selectTab, (val) => {
  if (val === null || val === undefined) {
    r_selectTab.value = 0
  }
})

const loadForecastingData = async () => {
  loading.value = true
  error.value = ''

  try {
    const [metricResponse, hourlyResponse, weeklyResponse, staffingResponse] = await Promise.all([
      ForecastingService.getForecastMetrics(),
      ForecastingService.getHourlyForecast(),
      ForecastingService.getWeeklyPerformance(),
      ForecastingService.getStaffingHeatmap(),
    ])

    console.debug('forecasting API responses', {
      metricResponse,
      hourlyResponse,
      weeklyResponse,
      staffingResponse,
    })

    metrics.value = metricResponse
    chartData.value = buildLineChart(hourlyResponse)
    barChartData.value = buildBarChart(weeklyResponse)
    radarChartData.value = buildRadarChart(staffingResponse)
    staffingData.value = staffingResponse
  } catch (err) {
    console.error('Failed to load forecasting data', err)
    error.value = 'Unable to load forecasting data.'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadForecastingData()
  chartOptions.value = setChartOptions()
  barChartOptions.value = setBarChartOptions()
  radarChartOptions.value = setRadarChartOptions()

  // Set up real-time refresh every 30 seconds
  refreshInterval = window.setInterval(() => {
    loadForecastingData()
  }, 30000)
})

onUnmounted(() => {
  if (refreshInterval) {
    clearInterval(refreshInterval)
    refreshInterval = null
  }
})

const buildLineChart = (items: ForecastTrendDTO[]) => {
  const documentStyle = getComputedStyle(document.documentElement)

  return {
    labels: items.map((item) => item.date),
    datasets: [
      {
        label: 'Demand Forecast',
        fill: false,
        borderColor: documentStyle.getPropertyValue('--p-cyan-500'),
        tension: 0.4,
        data: items.map((item) => item.predictedDemand),
      },
    ],
  }
}

const buildBarChart = (items: WeeklyForecastDTO[]) => {
  const documentStyle = getComputedStyle(document.documentElement)

  return {
    labels: items.map((item) => item.department),
    datasets: [
      {
        label: 'Performance',
        backgroundColor: documentStyle.getPropertyValue('--p-cyan-500'),
        data: items.map((item) => item.predictedDemand),
      },
      {
        label: 'Target',
        backgroundColor: documentStyle.getPropertyValue('--p-gray-500'),
        data: items.map((item) => item.actualDemand ?? 0),
      },
    ],
  }
}

const buildRadarChart = (items: StaffingHeatmapDTO[]) => {
  const documentStyle = getComputedStyle(document.documentElement)
  const textColor = documentStyle.getPropertyValue('--p-text-color')

  return {
    labels: items.map((item) => item.department),
    datasets: [
      {
        label: 'Morning',
        borderColor: documentStyle.getPropertyValue('--p-gray-400'),
        pointBackgroundColor: documentStyle.getPropertyValue('--p-gray-400'),
        pointBorderColor: documentStyle.getPropertyValue('--p-gray-400'),
        pointHoverBackgroundColor: textColor,
        pointHoverBorderColor: documentStyle.getPropertyValue('--p-gray-400'),
        data: items.map((item) => item.morning),
      },
      {
        label: 'Afternoon',
        borderColor: documentStyle.getPropertyValue('--p-pink-400'),
        pointBackgroundColor: documentStyle.getPropertyValue('--p-pink-400'),
        pointBorderColor: documentStyle.getPropertyValue('--p-pink-400'),
        pointHoverBackgroundColor: textColor,
        pointHoverBorderColor: documentStyle.getPropertyValue('--p-pink-400'),
        data: items.map((item) => item.afternoon),
      },
      {
        label: 'Night',
        borderColor: documentStyle.getPropertyValue('--p-cyan-500'),
        pointBackgroundColor: documentStyle.getPropertyValue('--p-cyan-500'),
        pointBorderColor: documentStyle.getPropertyValue('--p-cyan-500'),
        pointHoverBackgroundColor: textColor,
        pointHoverBorderColor: documentStyle.getPropertyValue('--p-cyan-500'),
        data: items.map((item) => item.night),
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
        labels: {
          color: textColor,
        },
      },
    },
    scales: {
      x: {
        ticks: {
          color: textColorSecondary,
        },
        grid: {
          color: surfaceBorder,
        },
      },
      y: {
        ticks: {
          color: textColorSecondary,
        },
        grid: {
          color: surfaceBorder,
        },
      },
    },
  }
}

const setBarChartOptions = () => {
  const documentStyle = getComputedStyle(document.documentElement)
  const textColor = documentStyle.getPropertyValue('--p-text-color')
  const textColorSecondary = documentStyle.getPropertyValue('--p-text-muted-color')
  const surfaceBorder = documentStyle.getPropertyValue('--p-content-border-color')

  return {
    maintainAspectRatio: false,
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
        },
        grid: {
          color: surfaceBorder,
        },
      },
      y: {
        beginAtZero: true,
        ticks: {
          color: textColorSecondary,
        },
        grid: {
          color: surfaceBorder,
        },
      },
    },
  }
}

const setRadarChartOptions = () => {
  const documentStyle = getComputedStyle(document.documentElement)
  const textColor = documentStyle.getPropertyValue('--p-text-color')
  const textColorSecondary = documentStyle.getPropertyValue('--p-text-muted-color')

  return {
    plugins: {
      legend: {
        labels: {
          color: textColor,
        },
      },
    },
    scales: {
      r: {
        grid: {
          color: textColorSecondary,
        },
      },
    },
  }
}
</script>

<style scoped src="./foreCastingComponent.css"></style>
