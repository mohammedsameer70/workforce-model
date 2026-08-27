<template>
  <div class="dashboard">
    <div class="dashboard">
      <h1>{{ lbl.employeeManagement }}</h1>
      <p>{{ lbl.employeeManagementDesc }}</p>
    </div>

    <!-- KPI Cards -->
    <div class="cardsInfo">
      <div class="card" v-for="metric in metrics" :key="metric.title">
        <div class="iconBox">
          <i :class="metric.icon" class="icon"></i>
        </div>

        <h3>{{ metric.title }}</h3>
        <p>{{ metric.value }}</p>
      </div>
    </div>
    <!-- Filters -->
    <div class="chartCard">
      <div style="display: flex; gap: 1rem; align-items: center; flex-wrap: wrap">
        <InputText v-model="search" placeholder="Search employees..." style="width: 300px" />

        <Select
          v-model="selectedDepartment"
          :options="departments"
          placeholder="Department"
          showClear
          style="width: 220px"
        />

        <Select
          v-model="selectedStatus"
          :options="statuses"
          placeholder="Status"
          showClear
          style="width: 180px"
        />

        <Button label="Clear" icon="pi pi-filter-slash" outlined @click="clearFilters" />
      </div>
    </div>

    <!-- Employee Table -->
    <div class="chartCard">
      <h3 style="margin-bottom: 1rem">Employee Directory & Workforce Performance</h3>

      <DataTable
        :value="filteredEmployees"
        paginator
        :rows="10"
        stripedRows
        tableStyle="min-width: 100%"
      >
        <Column header="Employee">
          <template #body="{ data }">
            <div style="display: flex; align-items: center; gap: 12px">
              <div class="avatar">
                {{ getInitials(data.name) }}
              </div>

              <div>
                <div style="font-weight: 600">
                  {{ data.name }}
                </div>
                <small>{{ data.employeeId }}</small>
              </div>
            </div>
          </template>
        </Column>

        <Column field="department" header="Department">
          <template #body="{ data }">
            <Tag :value="data.department" severity="contrast" />
          </template>
        </Column>

        <Column field="role" header="Role" />

        <Column header="Shift">
          <template #body="{ data }">
            <span
              class="shiftCell"
              :class="{
                morning: data.shift.includes('Morning'),
                afternoon: data.shift.includes('Afternoon'),
                night: data.shift.includes('Night'),
              }"
            >
              {{
                data.shift.includes('Morning')
                  ? 'Morning'
                  : data.shift.includes('Afternoon')
                    ? 'Afternoon'
                    : 'Night'
              }}
            </span>
          </template>
        </Column>

        <Column header="Utilization">
          <template #body="{ data }">
            <div style="display: flex; align-items: center; gap: 10px">
              <ProgressBar :value="data.utilization" :showValue="false" style="width: 120px" />
              <span>{{ data.utilization }}%</span>
            </div>
          </template>
        </Column>

        <Column header="Attendance">
          <template #body="{ data }">
            <strong>{{ data.attendance }}%</strong>
          </template>
        </Column>

        <Column header="Status">
          <template #body="{ data }">
            <Tag :value="data.status" :severity="getStatusSeverity(data.status)" />
          </template>
        </Column>
      </DataTable>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'

import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import Button from 'primevue/button'
import Tag from 'primevue/tag'
import ProgressBar from 'primevue/progressbar'
import { lbl } from '@/assets/constants/labels'
import EmployeesService from './employeesService'
import type { EmployeeDTO } from './employeesService'

const search = ref('')
const selectedDepartment = ref<string | null>(null)
const selectedStatus = ref<string | null>(null)
const departments = ref(['HR', 'IT', 'Finance', 'Operations', 'Marketing', 'Sales', 'Legal', 'Customer Service', 'Research & Development', 'Logistics', 'Quality Assurance', 'Administration'])
const statuses = ref(['Active', 'Leave'])
const employees = ref<EmployeeDTO[]>([])
const loading = ref(false)
const error = ref('')

const loadEmployees = async () => {
  loading.value = true
  error.value = ''

  try {
    employees.value = await EmployeesService.getEmployees()
  } catch (err) {
    console.error('Failed to load employees', err)
    error.value = 'Unable to load employees.'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadEmployees()
})

const filteredEmployees = computed(() => {
  return employees.value.filter((employee) => {
    const matchesSearch =
      !search.value ||
      employee.name.toLowerCase().includes(search.value.toLowerCase()) ||
      employee.employeeId.toLowerCase().includes(search.value.toLowerCase())

    const matchesDepartment =
      !selectedDepartment.value || employee.department === selectedDepartment.value

    const matchesStatus = !selectedStatus.value || employee.status === selectedStatus.value

    return matchesSearch && matchesDepartment && matchesStatus
  })
})

const metrics = computed(() => {
  const total = employees.value.length

  const active = employees.value.filter((e) => e.status === 'Active').length

  const avgUtilization = Math.round(
    employees.value.reduce((sum, e) => sum + (e.utilization ?? 0), 0) / Math.max(total, 1),
  )

  const avgAttendance = Math.round(
    employees.value.reduce((sum, e) => sum + Number(e.attendance ?? 0), 0) / Math.max(total, 1),
  )

  return [
    {
      title: 'Total Employees',
      value: total,
      icon: 'pi pi-users',
    },
    {
      title: 'Active Today',
      value: active,
      icon: 'pi pi-user-plus',
    },
    {
      title: 'Avg Utilization',
      value: `${avgUtilization}%`,
      icon: 'pi pi-clock',
    },
    {
      title: 'Avg Attendance',
      value: `${avgAttendance}%`,
      icon: 'pi pi-chart-line',
    },
  ]
})

const getInitials = (name: string) =>
  name
    .split(' ')
    .map((n) => n[0])
    .join('')
    .toUpperCase()

const getStatusSeverity = (status: string) => {
  switch (status) {
    case 'Active':
      return 'success'
    case 'Leave':
      return 'warn'
    default:
      return 'secondary'
  }
}

const clearFilters = () => {
  search.value = ''
  selectedDepartment.value = null
  selectedStatus.value = null
}
</script>

<style src="./employeesComponent.css" lang="css"></style>
