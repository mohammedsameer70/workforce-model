<template>
  <div class="capacityContainer">
    <!-- HEADER -->
    <div class="dashboardHeader">
      <div>
        <h1>Capacity Planning</h1>
        <p>Resource optimization, scalability analysis, and utilization forecasting</p>
      </div>
    </div>

    <!-- KPI CARDS -->
    <div class="metricsGrid">
      <div class="metricCard" v-for="metric in metrics" :key="metric.label">
        <div class="cardGlow"></div>

        <div class="cardTop">
          <div class="iconWrapper">
            <i :class="metric.icon"></i>
          </div>

          <span class="trend" :class="metric.trendType">
            {{ metric.trend }}
          </span>
        </div>

        <h2>{{ metric.value }}</h2>
        <p>{{ metric.label }}</p>
      </div>
    </div>

    <!-- UTILIZATION TREND -->
    <Panel class="customPanel">
      <template #header>
        <div class="panelHeader">
          <div>
            <h3>Capacity Utilization Trend</h3>
            <p>14-day rolling utilization vs capacity ceiling</p>
          </div>
        </div>
      </template>

      <Chart
        type="line"
        :data="capacityChartData"
        :options="capacityChartOptions"
        style="height: 400px"
      />
    </Panel>

    <!-- BOTTOM GRID -->
    <div class="bottomGrid">
      <!-- DEPARTMENT CAPACITY -->
      <Panel class="customPanel">
        <template #header>
          <div class="panelHeader">
            <div>
              <h3>Department Capacity</h3>
              <p>Current utilization by department</p>
            </div>
          </div>
        </template>

        <div class="departmentItem" v-for="dept in departments" :key="dept.name">
          <div class="departmentHeader">
            <span>{{ dept.name }}</span>

            <div class="departmentMeta">
              <strong>{{ dept.utilization }}%</strong>

              <span class="status" :class="dept.status.toLowerCase()">
                {{ dept.status }}
              </span>
            </div>
          </div>

          <div class="progressTrack">
            <div class="progressFill" :style="{ width: dept.utilization + '%' }"></div>
          </div>
        </div>
      </Panel>

      <!-- BENCHMARK -->
      <Panel class="customPanel">
        <template #header>
          <div class="panelHeader">
            <div>
              <h3>Scalability Benchmark</h3>
              <p>Response time under concurrent user load</p>
            </div>
          </div>
        </template>

        <Chart
          type="bar"
          :data="benchmarkChartData"
          :options="benchmarkChartOptions"
          style="height: 350px"
        />
      </Panel>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Chart from 'primevue/chart'
import Panel from 'primevue/panel'

const metrics = ref([
  {
    value: '78.4%',
    label: 'Capacity Load',
    trend: '1.2%',
    trendType: 'positive',
    icon: 'pi pi-sync',
  },
  {
    value: '92.1%',
    label: 'Peak Utilization',
    trend: '1.5%',
    trendType: 'negative',
    icon: 'pi pi-chart-line',
  },
  {
    value: '21.6%',
    label: 'Available Headroom',
    trend: '3.1%',
    trendType: 'positive',
    icon: 'pi pi-database',
  },
  {
    value: '4',
    label: 'Scaling Events',
    trend: '20%',
    trendType: 'negative',
    icon: 'pi pi-bolt',
  },
])

const departments = ref([
  {
    name: 'Inbound',
    utilization: 94,
    status: 'Critical',
  },
  {
    name: 'Outbound',
    utilization: 93,
    status: 'Critical',
  },
  {
    name: 'Sortation',
    utilization: 87,
    status: 'Critical',
  },
  {
    name: 'Packing',
    utilization: 64,
    status: 'Optimal',
  },
  {
    name: 'Returns',
    utilization: 72,
    status: 'Watch',
  },
  {
    name: 'Quality Control',
    utilization: 61,
    status: 'Optimal',
  },
])

const capacityChartData = ref()
const capacityChartOptions = ref()

const benchmarkChartData = ref()
const benchmarkChartOptions = ref()

onMounted(() => {
  capacityChartData.value = {
    labels: [
      'Day 1',
      'Day 2',
      'Day 3',
      'Day 4',
      'Day 5',
      'Day 6',
      'Day 7',
      'Day 8',
      'Day 9',
      'Day 10',
      'Day 11',
      'Day 12',
      'Day 13',
      'Day 14',
    ],
    datasets: [
      {
        label: 'Utilization',
        data: [82, 76, 88, 68, 75, 83, 67, 80, 83, 87, 81, 68, 68, 85],
        borderColor: '#38bdf8',
        backgroundColor: 'rgba(56,189,248,.15)',
        fill: true,
        tension: 0.4,
      },
      {
        label: 'Capacity',
        data: [84, 84, 89, 95, 94, 82, 94, 86, 84, 84, 92, 84, 84, 92],
        borderColor: '#22c55e',
        borderDash: [6, 6],
        tension: 0.4,
      },
    ],
  }

  capacityChartOptions.value = {
    maintainAspectRatio: false,
    responsive: true,
    plugins: {
      legend: {
        labels: {
          color: '#94a3b8',
        },
      },
    },
    scales: {
      x: {
        ticks: { color: '#94a3b8' },
        grid: { color: 'rgba(255,255,255,.05)' },
      },
      y: {
        min: 0,
        max: 100,
        ticks: { color: '#94a3b8' },
        grid: { color: 'rgba(255,255,255,.05)' },
      },
    },
  }

  benchmarkChartData.value = {
    labels: ['100', '200', '500', '1000', '2000', '5000'],
    datasets: [
      {
        label: 'Avg Response (ms)',
        backgroundColor: '#0ea5e9',
        borderRadius: 8,
        data: [22, 28, 42, 65, 94, 125],
      },
    ],
  }

  benchmarkChartOptions.value = {
    maintainAspectRatio: false,
    responsive: true,
    plugins: {
      legend: {
        labels: {
          color: '#94a3b8',
        },
      },
    },
    scales: {
      x: {
        ticks: { color: '#94a3b8' },
        grid: { color: 'rgba(255,255,255,.05)' },
      },
      y: {
        ticks: { color: '#94a3b8' },
        grid: { color: 'rgba(255,255,255,.05)' },
      },
    },
  }
})
</script>

<style scoped>
.capacityContainer {
  padding: 1.5rem;
}

.dashboardHeader h1 {
  margin: 0;
  font-size: 2rem;
}

.dashboardHeader p {
  margin-top: 0.4rem;
  color: #94a3b8;
}

.metricsGrid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem;
  margin: 1.5rem 0;
}

.metricCard {
  position: relative;
  overflow: hidden;
  border-radius: 18px;
  border: 1px solid #cfd1d4;
  padding: 1.5rem;
}

.cardGlow {
  position: absolute;
  width: 120px;
  height: 120px;
  right: -40px;
  top: -40px;
  border-radius: 50%;
  background: rgba(240, 239, 239, 0.899);
}

.cardTop {
  display: flex;
  justify-content: space-between;
}

.iconWrapper {
  width: 42px;
  height: 42px;
  border-radius: 12px;
  background: rgba(241, 241, 241, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
}

.iconWrapper i {
  color: #38bdf8;
  font-size: 1.1rem;
}

.metricCard h2 {
  margin: 1rem 0 0.3rem;
  font-size: 2rem;
}

.metricCard p {
  color: #94a3b8;
}

.trend {
  padding: 0.3rem 0.7rem;
  border-radius: 999px;
  font-size: 0.75rem;
  font-weight: 600;
}

.positive {
  color: #22c55e;
  background: rgba(34, 197, 94, 0.15);
}

.negative {
  color: #ef4444;
  background: rgba(239, 68, 68, 0.15);
}

.customPanel {
  margin-bottom: 1rem;
}

.panelHeader h3 {
  margin: 0;
}

.panelHeader p {
  margin-top: 0.2rem;
  color: #94a3b8;
  font-size: 0.85rem;
}

.bottomGrid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.departmentItem {
  margin-bottom: 1.25rem;
}

.departmentHeader {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
}

.departmentMeta {
  display: flex;
  gap: 0.75rem;
  align-items: center;
}

.progressTrack {
  width: 100%;
  height: 8px;
  background: #1e293b;
  border-radius: 999px;
  overflow: hidden;
}

.progressFill {
  height: 100%;
  background: linear-gradient(90deg, #0ea5e9, #38bdf8);
}

.status {
  font-size: 0.75rem;
  padding: 0.2rem 0.6rem;
  border-radius: 999px;
}

.critical {
  background: rgba(239, 68, 68, 0.15);
  color: #ef4444;
}

.watch {
  background: rgba(245, 158, 11, 0.15);
  color: #f59e0b;
}

.optimal {
  background: rgba(34, 197, 94, 0.15);
  color: #22c55e;
}

@media (max-width: 992px) {
  .metricsGrid {
    grid-template-columns: repeat(2, 1fr);
  }

  .bottomGrid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 640px) {
  .metricsGrid {
    grid-template-columns: 1fr;
  }
}
</style>
