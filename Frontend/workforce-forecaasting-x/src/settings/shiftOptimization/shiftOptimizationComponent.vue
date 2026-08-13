<template>
  <div class="dashboard">
    <div class="header">
      <h1>{{ lbl.ShiftOptimization }}</h1>
      <p>{{ lbl.ShiftOptimizationDesc }}</p>
    </div>
    <div class="headerEnd">
      <Button label="Run Optimization" icon="pi pi-play" @click="runOptimization" :loading="optimizing"></Button>
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

  <!-- Optimization Results Panel -->
  <Panel v-if="optimizationResult" header="Optimization Results" class="mb-4">
    <div class="optimization-summary">
      <div class="summary-item">
        <h4>Optimization Score</h4>
        <p class="score" :class="getScoreClass(optimizationResult.optimizationScore)">
          {{ optimizationResult.optimizationScore }}%
        </p>
      </div>
      <div class="summary-item">
        <h4>Employees Reassigned</h4>
        <p>{{ optimizationResult.employeesReassigned }}</p>
      </div>
      <div class="summary-item">
        <h4>Departments Optimized</h4>
        <p>{{ optimizationResult.departmentsOptimized }}</p>
      </div>
      <div class="summary-item">
        <h4>Estimated Savings</h4>
        <p :class="optimizationResult.dailySavings >= 0 ? 'positive' : 'negative'">
          {{ optimizationResult.dailySavings >= 0 ? '+' : '' }}${{ optimizationResult.dailySavings.toFixed(0) }}/day
        </p>
      </div>
      <div class="summary-item">
        <h4>Utilization</h4>
        <p>{{ optimizationResult.currentUtilization.toFixed(1) }}% → {{ optimizationResult.optimizedUtilization.toFixed(1) }}%</p>
      </div>
    </div>
  </Panel>

  <!-- Warnings Panel -->
  <Panel v-if="warnings.length > 0" header="Optimization Warnings" class="mb-4">
    <div class="warnings-list">
      <div v-for="warning in warnings" :key="warning.message" class="warning-item" :class="warning.severity">
        <i class="pi pi-exclamation-triangle"></i>
        <span>{{ warning.message }}</span>
      </div>
    </div>
  </Panel>

  <!-- Staff Allocations Table -->
  <Panel v-if="staffAllocations.length > 0" header="Staff Allocations" class="mb-4">
    <DataTable :value="staffAllocations" stripedRows responsiveLayout="scroll" class="modern-table">
      <Column field="department" header="Department">
        <template #body="{ data }">
          <span class="font-semibold">{{ data.department }}</span>
        </template>
      </Column>
      <Column field="predictedDemand" header="Predicted Demand">
        <template #body="{ data }">
          {{ data.predictedDemand.toFixed(0) }}
        </template>
      </Column>
      <Column field="currentStaff" header="Current Staff" />
      <Column field="recommendedStaff" header="Recommended Staff" />
      <Column field="surplus" header="Surplus">
        <template #body="{ data }">
          <Tag v-if="data.surplus > 0" :value="`+${data.surplus}`" severity="warning" />
          <span v-else>-</span>
        </template>
      </Column>
      <Column field="shortage" header="Shortage">
        <template #body="{ data }">
          <Tag v-if="data.shortage > 0" :value="`-${data.shortage}`" severity="danger" />
          <span v-else>-</span>
        </template>
      </Column>
    </DataTable>
  </Panel>

  <!-- Shift Assignments Table -->
  <Panel v-if="shiftAssignments.length > 0" header="Shift Assignments" class="mb-4">
    <DataTable :value="shiftAssignments" stripedRows responsiveLayout="scroll" class="modern-table">
      <Column field="shift" header="Shift">
        <template #body="{ data }">
          <span class="font-semibold">{{ data.shift }}</span>
        </template>
      </Column>
      <Column field="required" header="Required" />
      <Column field="assigned" header="Assigned" />
      <Column field="gap" header="Gap">
        <template #body="{ data }">
          <Tag :value="data.gap" :severity="data.gap > 0 ? 'danger' : data.gap < 0 ? 'warning' : 'success'" />
        </template>
      </Column>
    </DataTable>
  </Panel>

  <!-- Employee Reassignments -->
  <Panel v-if="reassignments.length > 0" header="Employee Reassignments" class="mb-4">
    <div class="reassignments-list">
      <div v-for="reassignment in reassignments" :key="reassignment.reason" class="reassignment-item">
        <div class="reassignment-flow">
          <div class="department-box from">
            <span>FROM</span>
            <strong>{{ reassignment.fromDepartment }}</strong>
          </div>
          <div class="arrow">
            <i class="pi pi-arrow-right"></i>
            <span class="count">{{ reassignment.employeesToMove }} employees</span>
          </div>
          <div class="department-box to">
            <span>TO</span>
            <strong>{{ reassignment.toDepartment }}</strong>
          </div>
        </div>
        <p class="reason">{{ reassignment.reason }}</p>
      </div>
    </div>
  </Panel>

  <!-- Optimization Recommendations -->
  <Panel v-if="optimizationRecommendations.length > 0" header="Optimization Recommendations" class="mb-4">
    <div class="recommendations-list">
      <div v-for="rec in optimizationRecommendations" :key="rec.details" class="recommendation-item">
        <Tag :value="rec.priority" :severity="getSeverity(rec.priority)" />
        <span>{{ rec.details }}</span>
      </div>
    </div>
  </Panel>

  <div v-if="loading" class="page-loading-overlay">
    <div class="page-loading-panel">
      <div class="page-loading-spinner"></div>
      <div>Loading shift optimization charts...</div>
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
  <Panel header="Add Shift Allocation" class="mb-4">
    <div class="allocationForm">
      <div class="formRow">
        <label>
          Department
          <input v-model="allocationForm.department" type="text" placeholder="Department name" />
        </label>

        <label>
          Time Slot
          <select v-model="allocationForm.timeSlot">
            <option>Morning</option>
            <option>Afternoon</option>
            <option>Night</option>
          </select>
        </label>

        <label>
          Day of Week
          <input v-model="allocationForm.dayOfWeek" type="text" placeholder="Monday" />
        </label>

        <label>
          Staffing Level
          <input v-model.number="allocationForm.staffingLevel" type="number" min="0" />
        </label>
      </div>

      <div class="formActions">
        <button
          type="button"
          class="primaryButton"
          :disabled="creatingAllocation"
          @click="submitShiftAllocation"
        >
          {{ creatingAllocation ? 'Saving...' : 'Add Shift Allocation' }}
        </button>
        <span class="formMessage" v-if="allocationError">{{ allocationError }}</span>
      </div>
    </div>
  </Panel>
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
import Chart from 'primevue/chart'
import Panel from 'primevue/panel'
import Button from 'primevue/button'
import Card from 'primevue/card'
import Tag from 'primevue/tag'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import { lbl } from '@/assets/constants/labels'
import ShiftOptimizationService from './shiftOptimizationService'
import type {
  ShiftMetricDTO,
  ShiftRecommendationDTO,
  ShiftCoverageDTO,
  ShiftAllocationDTO,
  StaffingBarPointDTO,
  OptimizationResultDTO,
  StaffAllocationDTO,
  ShiftAssignmentDTO,
  EmployeeReassignmentDTO,
  OptimizationWarningDTO,
  OptimizationRecommendationDTO,
} from './shiftOptimizationService'

const metrics = ref<ShiftMetricDTO[]>([])
const barChartData = ref<any>({ labels: [], datasets: [] })
const barChartOptions = ref<any>({})
const coverageData = ref<ShiftCoverageDTO[]>([])
const recommendations = ref<ShiftRecommendationDTO[]>([])
const allocationForm = ref<ShiftAllocationDTO>({
  department: '',
  timeSlot: 'Morning',
  dayOfWeek: '',
  staffingLevel: 0,
})
const creatingAllocation = ref(false)
const allocationError = ref('')
const loading = ref(false)
const error = ref('')
const optimizing = ref(false)
const optimizationResult = ref<OptimizationResultDTO | null>(null)
const staffAllocations = ref<StaffAllocationDTO[]>([])
const shiftAssignments = ref<ShiftAssignmentDTO[]>([])
const reassignments = ref<EmployeeReassignmentDTO[]>([])
const warnings = ref<OptimizationWarningDTO[]>([])
const optimizationRecommendations = ref<OptimizationRecommendationDTO[]>([])

const loadShiftOptimizationData = async () => {
  loading.value = true
  error.value = ''

  try {
    const [metricsResponse, staffingResponse, recommendationResponse, coverageResponse] =
      await Promise.all([
        ShiftOptimizationService.getMetrics(),
        ShiftOptimizationService.getStaffingByDepartment(),
        ShiftOptimizationService.getRecommendations(),
        ShiftOptimizationService.getCoverage(),
      ])

    metrics.value = metricsResponse
    barChartData.value = buildBarChart(staffingResponse)
    recommendations.value = recommendationResponse
    coverageData.value = coverageResponse
  } catch (err) {
    console.error('Failed to load shift optimization data', err)
    error.value = 'Unable to load shift optimization data.'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadShiftOptimizationData()
  barChartOptions.value = setBarChartOptions()
})

const buildBarChart = (items: StaffingBarPointDTO[]) => {
  const documentStyle = getComputedStyle(document.documentElement)

  return {
    labels: items.map((item) => item.department),
    datasets: [
      {
        label: 'Current',
        backgroundColor: documentStyle.getPropertyValue('--p-cyan-500'),
        data: items.map((item) => item.current),
      },
      {
        label: 'Optimal',
        backgroundColor: documentStyle.getPropertyValue('--p-gray-500'),
        data: items.map((item) => item.optimal),
      },
    ],
  }
}

const dismissRecommendation = (id: number) => {
  recommendations.value = recommendations.value.filter((item) => item.id !== id)
}

const resetAllocationForm = () => {
  allocationForm.value = {
    department: '',
    timeSlot: 'Morning',
    dayOfWeek: '',
    staffingLevel: 0,
  }
}

const submitShiftAllocation = async () => {
  allocationError.value = ''
  creatingAllocation.value = true

  if (!allocationForm.value.department?.trim()) {
    allocationError.value = 'Department is required.'
    creatingAllocation.value = false
    return
  }

  if (!allocationForm.value.timeSlot?.trim()) {
    allocationError.value = 'Time slot is required.'
    creatingAllocation.value = false
    return
  }

  try {
    const allocation: StaffAllocationDTO = {
      department: allocationForm.value.department.trim(),
      predictedDemand: 0,
      currentStaff: allocationForm.value.staffingLevel ?? 0,
      recommendedStaff: allocationForm.value.staffingLevel ?? 0,
      surplus: 0,
      shortage: 0
    }
    
    await ShiftOptimizationService.createAllocation(allocation)

    // Refresh the optimization data to show the new allocation
    if (optimizationResult.value) {
      await runOptimization()
    } else {
      await loadShiftOptimizationData()
    }
    
    resetAllocationForm()
  } catch (err) {
    console.error('Unable to create shift allocation', err)
    allocationError.value = 'Unable to save shift allocation.'
  } finally {
    creatingAllocation.value = false
  }
}

const applyRecommendation = async (item: ShiftRecommendationDTO) => {
  try {
    // Convert ShiftRecommendationDTO to OptimizationRecommendationDTO
    const optRecommendation: OptimizationRecommendationDTO = {
      action: 'REASSIGN_STAFF',
      details: `Move ${item.workers} from ${item.from} to ${item.to}`,
      priority: item.priority
    }
    
    await ShiftOptimizationService.applyRecommendation(optRecommendation)
    
    // Remove the recommendation from the list
    recommendations.value = recommendations.value.filter((r) => r.id !== item.id)
    
    // Show success message (could add a toast notification here)
    console.log('Recommendation applied successfully')
  } catch (err) {
    console.error('Failed to apply recommendation', err)
    error.value = 'Unable to apply recommendation.'
  }
}

const runOptimization = async () => {
  optimizing.value = true
  try {
    const result = await ShiftOptimizationService.runOptimization()
    optimizationResult.value = result
    
    // Load additional optimization data
    const [allocations, assignments, reassigns, warn, recs] = await Promise.all([
      ShiftOptimizationService.getStaffAllocations(),
      ShiftOptimizationService.getShiftAssignments(),
      ShiftOptimizationService.getReassignments(),
      ShiftOptimizationService.getWarnings(),
      ShiftOptimizationService.getOptimizationRecommendations(),
    ])
    
    staffAllocations.value = allocations
    shiftAssignments.value = assignments
    reassignments.value = reassigns
    warnings.value = warn
    optimizationRecommendations.value = recs
  } catch (err) {
    console.error('Failed to run optimization', err)
    error.value = 'Unable to run optimization.'
  } finally {
    optimizing.value = false
  }
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

const getScoreClass = (score: number) => {
  if (score >= 80) return 'high'
  if (score >= 60) return 'medium'
  return 'low'
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
</script>

<style scoped src="./shiftOptimizationComponent.css"></style>
