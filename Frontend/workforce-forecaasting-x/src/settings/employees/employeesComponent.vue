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
                <small>{{ data.id }}</small>
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
import { ref, computed } from 'vue'

import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import InputText from 'primevue/inputtext'
import Select from 'primevue/select'
import Button from 'primevue/button'
import Tag from 'primevue/tag'
import ProgressBar from 'primevue/progressbar'
import { lbl } from '@/assets/constants/labels'

const search = ref('')
const selectedDepartment = ref(null)
const selectedStatus = ref(null)

const departments = ['Inbound', 'Outbound', 'Packing', 'Returns', 'Sortation', 'Quality Control']

const statuses = ['Active', 'Leave']

const employees = ref([
  {
    id: 'EMP-1001',
    name: 'James Wilson',
    department: 'Returns',
    role: 'QC Inspector',
    shift: 'Morning (06:00-14:00)',
    utilization: 84,
    attendance: 89,
    status: 'Active',
  },
  {
    id: 'EMP-1002',
    name: 'Sarah Chen',
    department: 'Outbound',
    role: 'Forklift Operator',
    shift: 'Night (22:00-06:00)',
    utilization: 60,
    attendance: 87,
    status: 'Active',
  },
  {
    id: 'EMP-1003',
    name: 'Marcus Johnson',
    department: 'Inbound',
    role: 'Picker',
    shift: 'Morning (06:00-14:00)',
    utilization: 65,
    attendance: 87,
    status: 'Active',
  },
  {
    id: 'EMP-1004',
    name: 'Emily Davis',
    department: 'Returns',
    role: 'QC Inspector',
    shift: 'Night (22:00-06:00)',
    utilization: 71,
    attendance: 86,
    status: 'Active',
  },
  {
    id: 'EMP-1005',
    name: 'David Brown',
    department: 'Outbound',
    role: 'Team Lead',
    shift: 'Morning (06:00-14:00)',
    utilization: 78,
    attendance: 90,
    status: 'Active',
  },
  {
    id: 'EMP-1006',
    name: 'Lisa Wang',
    department: 'Packing',
    role: 'Packer',
    shift: 'Afternoon (14:00-22:00)',
    utilization: 72,
    attendance: 86,
    status: 'Leave',
  },
  {
    id: 'EMP-1007',
    name: 'Robert Taylor',
    department: 'Inbound',
    role: 'Team Lead',
    shift: 'Night (22:00-06:00)',
    utilization: 94,
    attendance: 91,
    status: 'Active',
  },
  {
    id: 'EMP-1008',
    name: 'Anna Martinez',
    department: 'Outbound',
    role: 'Packer',
    shift: 'Afternoon (14:00-22:00)',
    utilization: 93,
    attendance: 88,
    status: 'Active',
  },
  {
    id: 'EMP-1009',
    name: 'Michael Lee',
    department: 'Sortation',
    role: 'Packer',
    shift: 'Morning (06:00-14:00)',
    utilization: 84,
    attendance: 93,
    status: 'Active',
  },
  {
    id: 'EMP-1010',
    name: 'Jennifer White',
    department: 'Quality Control',
    role: 'Packer',
    shift: 'Morning (06:00-14:00)',
    utilization: 87,
    attendance: 89,
    status: 'Active',
  },
])

const filteredEmployees = computed(() => {
  return employees.value.filter((employee) => {
    const matchesSearch =
      !search.value ||
      employee.name.toLowerCase().includes(search.value.toLowerCase()) ||
      employee.id.toLowerCase().includes(search.value.toLowerCase())

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
    employees.value.reduce((sum, e) => sum + e.utilization, 0) / total,
  )

  const avgAttendance = Math.round(
    employees.value.reduce((sum, e) => sum + e.attendance, 0) / total,
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
