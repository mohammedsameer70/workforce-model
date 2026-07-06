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
          <Chart type="line" :data="chartData" :options="chartOptions" style="height: 420px" />
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
const metrics = ref([
  {
    title: '4,280/hr',
    value: 'Avg Throughput',
    icon: 'pi pi-chart-bar',
  },
  {
    title: '89.4%',
    value: 'Productivity Index',
    icon: 'pi pi-bullseye',
  },
  {
    title: '91.2%',
    value: 'Workforce Efficiency',
    icon: 'pi pi-users',
  },
  {
    title: '2,847',
    value: 'Avg Shift Duration',
    icon: 'pi pi-clock',
  },
])
const throughputChartData = ref()
const throughputChartOptions = ref()

const chartData = ref()
const chartOptions = ref()

const barChartData = ref()
const barChartOptions = ref()

const radarChartData = ref()
const radarChartOptions = ref()
const departmentChartData = ref()

const setDepartmentChartData = () => {
  return {
    labels: ['Inbound', 'Outbound', 'Sortation', 'Packing', 'Returns', 'Quality Control'],

    datasets: [
      {
        data: [18, 22, 14, 20, 16, 10],

        backgroundColor: [
          '#0EA5E9', // blue
          '#34D399', // green
          '#A855F7', // purple
          '#F59E0B', // orange
          '#F43F5E', // pink
          '#2563EB', // dark blue
        ],

        borderColor: '#0B1220',
        borderWidth: 3,
        hoverOffset: 8,
      },
    ],
  }
}
const departmentChartOptions = ref()

const setDepartmentChartOptions = () => {
  return {
    cutout: '65%',

    plugins: {
      legend: {
        position: 'bottom',
        labels: {
          color: '#94A3B8',
          usePointStyle: true,
          pointStyle: 'circle',
          padding: 16,
        },
      },

      tooltip: {
        backgroundColor: '#0B1220',
        borderColor: '#1F2937',
        borderWidth: 1,
      },
    },
  }
}
const weeklyChartData = ref()

const setWeeklyChartData = () => {
  return {
    labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],

    datasets: [
      {
        label: 'Demand',
        data: [980, 1050, 920, 1150, 1100, 1080, 1000],
        backgroundColor: '#00B2FF',
        borderRadius: 6,
      },

      {
        label: 'Staffed',
        data: [920, 850, 980, 940, 830, 900, 860],
        backgroundColor: '#34D399',
        borderRadius: 6,
      },
    ],
  }
}
const weeklyChartOptions = ref()

const setWeeklyChartOptions = () => {
  return {
    responsive: true,
    maintainAspectRatio: false,

    plugins: {
      legend: {
        position: 'bottom',
        labels: {
          color: '#94A3B8',
          usePointStyle: true,
          pointStyle: 'circle',
        },
      },
    },

    scales: {
      x: {
        ticks: { color: '#64748B' },
        grid: { color: 'rgba(148,163,184,0.08)' },
      },

      y: {
        beginAtZero: true,
        ticks: { color: '#64748B' },
        grid: { color: 'rgba(148,163,184,0.08)' },
      },
    },
  }
}

onMounted(() => {
  chartData.value = setChartData()
  chartOptions.value = setChartOptions()
  throughputChartData.value = buildThroughputChartData()
  throughputChartOptions.value = buildThroughputChartOptions()
  barChartData.value = setBarChartData()
  barChartOptions.value = setBarChartOptions()
  departmentChartData.value = setDepartmentChartData()
  departmentChartOptions.value = setDepartmentChartOptions()

  weeklyChartData.value = setWeeklyChartData()
  weeklyChartOptions.value = setWeeklyChartOptions()
  radarChartData.value = setRadarChartData()
  radarChartOptions.value = setRadarChartOptions()
})
const buildThroughputChartData = () => {
  return {
    labels: [
      '00:00',
      '01:00',
      '02:00',
      '03:00',
      '04:00',
      '05:00',
      '06:00',
      '07:00',
      '08:00',
      '09:00',
      '10:00',
      '11:00',
      '12:00',
      '13:00',
      '14:00',
      '15:00',
      '16:00',
      '17:00',
      '18:00',
      '19:00',
      '20:00',
      '21:00',
      '22:00',
      '23:00',
    ],

    datasets: [
      {
        label: 'Orders',
        data: [
          800, 700, 1200, 900, 1300, 1000, 1100, 1200, 3800, 3000, 4000, 3700, 3400, 3800, 3750,
          3500, 4300, 4700, 3200, 3300, 2500, 800, 900, 1200,
        ],
        borderColor: '#00B2FF',
        backgroundColor: 'rgba(0,178,255,0.15)',
        fill: true,
        tension: 0.45,
        borderWidth: 2,
        pointRadius: 0,
      },

      {
        label: 'Packages',
        data: [
          900, 800, 1500, 1000, 1500, 1200, 1300, 1300, 4800, 3800, 5000, 4800, 4300, 4900, 4800,
          4500, 5600, 6100, 4200, 4200, 3500, 900, 1100, 1500,
        ],
        borderColor: '#10D9A5',
        fill: false,
        tension: 0.45,
        borderWidth: 2,
        pointRadius: 0,
      },
    ],
  }
}
const buildThroughputChartOptions = () => {
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
          color: '#94A3B8',
          usePointStyle: true,
          pointStyle: 'circle',
        },
      },
    },

    scales: {
      x: {
        ticks: { color: '#64748B' },
        grid: { color: 'rgba(148,163,184,0.08)' },
      },

      y: {
        beginAtZero: true,
        ticks: { color: '#64748B' },
        grid: { color: 'rgba(148,163,184,0.08)' },
      },
    },
  }
}

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
  return {
    labels: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'],

    datasets: [
      {
        label: 'Productivity %',
        data: [83, 79, 80, 75, 90, 87, 84, 84, 88, 78, 75, 86],
        borderColor: '#00B2FF',
        backgroundColor: '#00B2FF',
        borderWidth: 3,
        tension: 0.45,
        pointRadius: 0,
        pointHoverRadius: 6,
      },

      {
        label: 'Target',
        data: [90, 90, 90, 90, 90, 90, 90, 90, 90, 90, 90, 90],
        borderColor: '#10D9A5',
        borderWidth: 2,
        borderDash: [6, 6],
        pointRadius: 0,
        tension: 0,
      },

      {
        label: 'Overtime %',
        data: [14, 13, 19, 8, 19, 14, 7, 11, 14, 9, 5, 19],
        borderColor: '#FF2E88',
        backgroundColor: '#FF2E88',
        borderWidth: 3,
        tension: 0.45,
        pointRadius: 0,
        pointHoverRadius: 6,
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
          color: '#94A3B8',
          usePointStyle: true,
          pointStyle: 'circle',
          padding: 20,
        },
      },

      tooltip: {
        backgroundColor: '#0F172A',
        borderColor: '#1E293B',
        borderWidth: 1,
      },
    },

    scales: {
      x: {
        ticks: {
          color: '#64748B',
        },

        grid: {
          color: 'rgba(148,163,184,0.08)',
        },

        border: {
          color: '#334155',
        },
      },

      y: {
        min: 0,
        max: 100,

        ticks: {
          color: '#64748B',
          stepSize: 25,
        },

        grid: {
          color: 'rgba(148,163,184,0.08)',
        },

        border: {
          color: '#334155',
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
<style scoped src="./analyticsComponent.css"></style>
