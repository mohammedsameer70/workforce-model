<template>
  <div class="dashboard">
    <div class="header">
      <h1>{{ lbl.ShiftOptimization }}</h1>
      <p>{{ lbl.ShiftOptimizationDesc }}</p>
    </div>
    <div class="headerEnd">
      <Button label="lbl.runOptimization" icon="pi pi-calendar-clock"></Button>
    </div>
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
  <div class="flex flex-row">
    <div class="col-6">
      <Panel
        header="Staffing by Department & Shift"
        description="Current vs optimal allocation"
        class="mb-4"
      >
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
    </div>
    <div class="col-6">
      <Panel style="height: 100%" header="AI Recommendations" class="mb-4">
        <div class="recommendation-scroll">
          <div class="recommendation-grid">
            <Card
              v-for="item in recommendations"
              :key="item.id"
              class="recommendation-card"
              :class="item.priority"
            >
              <template #content>
                <div class="recommendation-header">
                  <div class="title-section">
                    <div class="ai-icon">
                      <i class="pi pi-sparkles"></i>
                    </div>

                    <div>
                      <h4>{{ item.title }}</h4>

                      <Tag
                        :value="item.priority.toUpperCase()"
                        :severity="getSeverity(item.priority)"
                      />
                    </div>
                  </div>

                  <div class="worker-impact">
                    <i class="pi pi-users"></i>
                    {{ item.workers }}
                  </div>
                </div>

                <div class="flow-section">
                  <div class="department-box">
                    <span>FROM</span>
                    <strong>{{ item.from }}</strong>
                  </div>

                  <i class="pi pi-arrow-right transfer-arrow"></i>

                  <div class="department-box target">
                    <span>TO</span>
                    <strong>{{ item.to }}</strong>
                  </div>
                </div>

                <div class="action-section">
                  <Button
                    label="Dismiss"
                    icon="pi pi-times"
                    severity="secondary"
                    outlined
                    @click="dismissRecommendation(item.id)"
                  />

                  <Button label="Apply" icon="pi pi-check" @click="applyRecommendation(item)" />
                </div>
              </template>
            </Card>
          </div>
        </div>
      </Panel>
    </div>
  </div>
  <div class="coverage-card">
    <div class="header">
      <h2>Shift Coverage Matrix</h2>
      <p>Current allocation with gap analysis</p>
    </div>

    <DataTable :value="coverageData" stripedRows responsiveLayout="scroll" class="modern-table">
      <Column field="department" header="Department">
        <template #body="{ data }">
          <span class="font-semibold">{{ data.department }}</span>
        </template>
      </Column>

      <Column field="morning" header="Morning (06:00-14:00)" />

      <Column field="afternoon" header="Afternoon (14:00-22:00)" />

      <Column field="night" header="Night (22:00-06:00)" />

      <Column field="status" header="Status">
        <template #body="{ data }">
          <Tag :value="data.status" severity="danger" rounded />
        </template>
      </Column>
    </DataTable>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

// Import PrimeVue components (They register automatically in <script setup>)
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
const coverageData = [
  {
    department: 'Inbound',
    morning: '37 / 37',
    afternoon: '42 / 52',
    night: '10 / 21',
    status: 'Gap',
  },
  {
    department: 'Outbound',
    morning: '33 / 40',
    afternoon: '54 / 34',
    night: '21 / 28',
    status: 'Gap',
  },
  {
    department: 'Sortation',
    morning: '25 / 36',
    afternoon: '56 / 55',
    night: '17 / 28',
    status: 'Gap',
  },
  {
    department: 'Packing',
    morning: '39 / 25',
    afternoon: '28 / 55',
    night: '24 / 18',
    status: 'Gap',
  },
  {
    department: 'Returns',
    morning: '34 / 35',
    afternoon: '56 / 58',
    night: '21 / 20',
    status: 'Gap',
  },
  {
    department: 'Quality Control',
    morning: '49 / 46',
    afternoon: '52 / 57',
    night: '14 / 21',
    status: 'Gap',
  },
]
const recommendations = ref([
  {
    id: 1,
    title: 'Suggested Shift Reallocation',
    priority: 'high',
    workers: '+5 workers',
    from: 'Inbound Morning',
    to: 'Outbound Afternoon',
  },
  {
    id: 2,
    title: 'Peak Demand Projected',
    priority: 'medium',
    workers: '+3 workers',
    from: 'Returns Night',
    to: 'Sortation Night',
  },
  {
    id: 3,
    title: 'Low Returns Volume',
    priority: 'high',
    workers: '+4 workers',
    from: 'QC Morning',
    to: 'Packing Morning',
  },
  {
    id: 4,
    title: 'Backlog Clearance',
    priority: 'low',
    workers: '+2 workers',
    from: 'Sortation Afternoon',
    to: 'Outbound Afternoon',
  },
  {
    id: 5,
    title: 'Overtime Prevention',
    priority: 'medium',
    workers: '+3 workers',
    from: 'Packing Night',
    to: 'Inbound Morning',
  },
])

const dismissRecommendation = (id: number) => {
  recommendations.value = recommendations.value.filter((item) => item.id !== id)
}

const applyRecommendation = (item: any) => {
  console.log('Applying recommendation:', item)
}

const getSeverity = (priority: string) => {
  switch (priority) {
    case 'high':
      return 'danger'
    case 'medium':
      return 'warning'
    case 'low':
      return 'success'
    default:
      return 'info'
  }
}
const barChartData = ref()
const barChartOptions = ref()
onMounted(() => {
  barChartData.value = setBarChartData()
  barChartOptions.value = setBarChartOptions()
})
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
const metrics = ref([
  {
    title: lbl.ShiftCoverage,
    value: lbl.ShiftCoverage,
    icon: 'pi pi-calendar-clock',
  },
  {
    title: '94.2%',
    value: lbl.ShiftCoverage,
    icon: 'pi pi-users',
  },
  {
    title: '3.8',
    value: lbl.OptimizationScore,
    icon: 'pi pi-chart-line',
  },
  {
    title: '2,847',
    value: lbl.ShiftConflicts,
    icon: 'pi pi-exclamation-triangle',
  },
])

onMounted(() => {
  console.log('Component mounted using inline template!')
})
</script>

<style scoped src="./shiftOptimizationComponent.css"></style>
