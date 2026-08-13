import api from '@/services/apiClient'

export interface EmployeeDTO {
  id: number
  employeeId: string
  name: string
  gender: string
  dateOfBirth: string | null
  age: number | null
  email: string | null
  phoneNumber: string | null
  department: string
  role: string
  employmentType: string
  team: string
  manager: string | null
  branch: string
  location: string
  shift: string
  preferredShift: string
  experienceYears: number | null
  joinDate: string | null
  status: string
  attendance: string
  utilization: number | null
  lastUpdated: string
}

export interface EmployeeFilterOptionDTO {
  departments: string[]
  statuses: string[]
}

class EmployeesService {
  async getEmployees(): Promise<EmployeeDTO[]> {
    const response = await api.get<EmployeeDTO[]>('/employees')
    return response.data
  }

  async getEmployeeFilters(): Promise<EmployeeFilterOptionDTO> {
    const response = await api.get<EmployeeDTO[]>('/employees')
    const employees = response.data

    return {
      departments: Array.from(new Set(employees.map((emp) => emp.department).filter(Boolean))).sort(),
      statuses: Array.from(new Set(employees.map((emp) => emp.status).filter(Boolean))).sort(),
    }
  }
}

export default new EmployeesService()
