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

  <SelectButton
    v-model="r_selectTab"
    :options="options"
    optionLabel="label"
    optionValue="value"
    fluid
  />

  <!-- 24 HOURS FORECAST -->
  <Panel v-if="r_selectTab === 0" :header="lbl.HourlyDemandPrediction" class="mb-4">
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
  <Panel v-if="r_selectTab === 1" header="Department Performance" class="mb-4">
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
import { ref, onMounted } from 'vue'

import Chart from 'primevue/chart'
import Panel from 'primevue/panel'
import SelectButton from 'primevue/selectbutton'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'

import { lbl } from '@/assets/constants/labels'

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
  },
  {
    label: lbl.weeklyForeCast,
    value: 1,
  },
]

const chartData = ref()
const chartOptions = ref()

const barChartData = ref()
const barChartOptions = ref()

const radarChartData = ref()
const radarChartOptions = ref()

onMounted(() => {
  chartData.value = setChartData()
  chartOptions.value = setChartOptions()

  barChartData.value = setBarChartData()
  barChartOptions.value = setBarChartOptions()

  radarChartData.value = setRadarChartData()
  radarChartOptions.value = setRadarChartOptions()
})

const setRadarChartData = () => {
  const documentStyle = getComputedStyle(document.documentElement)
  const textColor = documentStyle.getPropertyValue('--p-text-color')

  return {
    labels: ['Eating', 'Drinking', 'Sleeping', 'Designing', 'Coding', 'Cycling', 'Running'],
    datasets: [
      {
        label: 'My First dataset',
        borderColor: documentStyle.getPropertyValue('--p-gray-400'),
        pointBackgroundColor: documentStyle.getPropertyValue('--p-gray-400'),
        pointBorderColor: documentStyle.getPropertyValue('--p-gray-400'),
        pointHoverBackgroundColor: textColor,
        pointHoverBorderColor: documentStyle.getPropertyValue('--p-gray-400'),
        data: [65, 59, 90, 81, 56, 55, 40],
      },
      {
        label: 'My Second dataset',
        borderColor: documentStyle.getPropertyValue('--p-pink-400'),
        pointBackgroundColor: documentStyle.getPropertyValue('--p-pink-400'),
        pointBorderColor: documentStyle.getPropertyValue('--p-pink-400'),
        pointHoverBackgroundColor: textColor,
        pointHoverBorderColor: documentStyle.getPropertyValue('--p-pink-400'),
        data: [28, 48, 40, 19, 96, 27, 100],
      },
    ],
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

const setChartData = () => {
  const documentStyle = getComputedStyle(document.documentElement)

  return {
    labels: ['January', 'February', 'March', 'April', 'May', 'June', 'July'],
    datasets: [
      {
        label: 'Demand Forecast',
        fill: false,
        borderColor: documentStyle.getPropertyValue('--p-cyan-500'),
        tension: 0.4,
        data: [65, 59, 80, 81, 56, 55, 10],
      },
      {
        label: 'Actual Demand',
        fill: false,
        borderColor: documentStyle.getPropertyValue('--p-gray-500'),
        tension: 0.4,
        data: [28, 48, 40, 19, 86, 27, 90],
      },
    ],
  }
}

const setBarChartData = () => {
  const documentStyle = getComputedStyle(document.documentElement)

  return {
    labels: ['Inbound', 'Outbound', 'Sortation', 'Packing', 'Returns', 'Quality'],
    datasets: [
      {
        label: 'Performance',
        backgroundColor: documentStyle.getPropertyValue('--p-cyan-500'),
        data: [65, 59, 80, 81, 56, 55],
      },
      {
        label: 'Target',
        backgroundColor: documentStyle.getPropertyValue('--p-gray-500'),
        data: [70, 70, 70, 70, 70, 70],
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
])

const metrics = ref([
  {
    title: 'LSTM v3.2',
    value: 'Active Model',
    icon: 'pi pi-bolt',
  },
  {
    title: '94.2%',
    value: 'Accuracy (MAPE)',
    icon: 'pi pi-bullseye',
  },
  {
    title: '3.8',
    value: 'RMSE Score',
    icon: 'pi pi-chart-line',
  },
  {
    title: '2,847',
    value: 'Predictions Today',
    icon: 'pi pi-microchip-ai',
  },
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
])
</script>

<style scoped src="./foreCastingComponent.css"></style>
