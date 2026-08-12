import api from '@/services/apiClient'

export interface ShiftMetricDTO {
  title: string
  value: string
  icon: string
}

export interface ShiftRecommendationDTO {
  id: number
  title: string
  priority: string
  workers: string
  from: string
  to: string
}

export interface ShiftCoverageDTO {
  department: string
  morning: string
  afternoon: string
  night: string
  status: string
}

export interface ShiftAllocationDTO {
  department: string
  timeSlot: string
  dayOfWeek?: string | null
  staffingLevel?: number | null
}

export interface StaffingBarPointDTO {
  department: string
  current: number
  optimal: number
}

export interface OptimizationResultDTO {
  optimizationScore: number
  employeesReassigned: number
  departmentsOptimized: number
  estimatedSavingsPercentage: number
  currentUtilization: number
  optimizedUtilization: number
  dailyCostBefore: number
  dailyCostAfter: number
  dailySavings: number
}

export interface StaffAllocationDTO {
  department: string
  predictedDemand: number
  currentStaff: number
  recommendedStaff: number
  surplus: number
  shortage: number
}

export interface ShiftAssignmentDTO {
  shift: string
  required: number
  assigned: number
  gap: number
}

export interface EmployeeReassignmentDTO {
  employeesToMove: number
  fromDepartment: string
  toDepartment: string
  reason: string
}

export interface OptimizationWarningDTO {
  type: string
  message: string
  department: string
  severity: string
}

export interface OptimizationRecommendationDTO {
  action: string
  details: string
  priority: string
}

interface ShiftStaffingBackendDTO {
  id: number
  shift: string
  department: string
  requiredStaff: number | null
  currentStaff: number | null
  gap: number | null
  utilization: number | null
  date: string | null
  createdAt: string | null
}

interface AIRecommendationBackendDTO {
  id: number
  title: string
  description: string
  type: string
  priority: string
  department: string
  shift: string
  recommendedStaff: number | null
  currentStaff: number | null
  status: string
  createdAt: string | null
}

interface ShiftCoverageBackendDTO {
  department: string
  shift: string
  dayOfWeek: string
  coveragePercentage: number | null
  staffCount: number | null
  demandCount: number | null
  status: string
}

class ShiftOptimizationService {
  async getMetrics(): Promise<ShiftMetricDTO[]> {
    const response = await api.get<ShiftMetricDTO[]>('/shift-optimization/metrics')
    return response.data
  }

  async getStaffingByDepartment(): Promise<StaffingBarPointDTO[]> {
    const response = await api.get<StaffingBarPointDTO[]>('/shift-optimization/staffing-by-department')
    return response.data
  }

  async getRecommendations(): Promise<ShiftRecommendationDTO[]> {
    const response = await api.get<ShiftRecommendationDTO[]>('/shift-optimization/recommendations')
    return response.data
  }

  async getCoverage(): Promise<ShiftCoverageDTO[]> {
    const response = await api.get<ShiftCoverageDTO[]>('/shift-optimization/coverage')
    return response.data
  }

  async createShiftAllocation(entry: ShiftAllocationDTO): Promise<ShiftAllocationDTO> {
    const response = await api.post<ShiftAllocationDTO>('/forecasting/staffing-heatmap', entry)
    return response.data
  }

  async runOptimization(): Promise<OptimizationResultDTO> {
    const response = await api.post<OptimizationResultDTO>('/optimization/run')
    return response.data
  }

  async getOptimizationResult(): Promise<OptimizationResultDTO> {
    const response = await api.get<OptimizationResultDTO>('/optimization/result')
    return response.data
  }

  async getStaffAllocations(): Promise<StaffAllocationDTO[]> {
    const response = await api.get<StaffAllocationDTO[]>('/optimization/staff-allocations')
    return response.data
  }

  async getShiftAssignments(): Promise<ShiftAssignmentDTO[]> {
    const response = await api.get<ShiftAssignmentDTO[]>('/optimization/shift-assignments')
    return response.data
  }

  async getReassignments(): Promise<EmployeeReassignmentDTO[]> {
    const response = await api.get<EmployeeReassignmentDTO[]>('/optimization/reassignments')
    return response.data
  }

  async getWarnings(): Promise<OptimizationWarningDTO[]> {
    const response = await api.get<OptimizationWarningDTO[]>('/optimization/warnings')
    return response.data
  }

  async getOptimizationRecommendations(): Promise<OptimizationRecommendationDTO[]> {
    const response = await api.get<OptimizationRecommendationDTO[]>('/optimization/recommendations')
    return response.data
  }

  async applyRecommendation(recommendation: OptimizationRecommendationDTO): Promise<void> {
    await api.post<void>('/optimization/apply-recommendation', recommendation)
  }

  async createAllocation(allocation: StaffAllocationDTO): Promise<StaffAllocationDTO> {
    const response = await api.post<StaffAllocationDTO>('/optimization/create-allocation', allocation)
    return response.data
  }
}

export default new ShiftOptimizationService()
