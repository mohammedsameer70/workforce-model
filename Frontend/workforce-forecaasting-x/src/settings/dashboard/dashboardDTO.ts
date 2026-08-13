export interface DashboardMetricDTO {
  id?: number
  title: string
  value: string
  trend?: string | null
  trendDirection?: string | null
  unit?: string | null
  category?: string | null
  lastUpdated?: string | null
}

export interface DepartmentPerformanceDTO {
  department: string
  averagePrediction: number
}

export interface PredictionTrendDTO {
  date: string
  predictedDemand: number
}

export interface RecentPredictionDTO {
  id: number
  date: string | null
  department: string
  actualDemand: number
  predictedDemand: number
}

export interface DashboardSummaryDTO {
  model: string | null
  totalRecords: number
  averagePrediction: number
  maximumPrediction: number
  minimumPrediction: number
}

export interface DashboardDataDTO {
  summary: DashboardSummaryDTO
  departments: DepartmentPerformanceDTO[]
  trend: PredictionTrendDTO[]
  recentPredictions: RecentPredictionDTO[]
}

export interface AlertDTO {
  id?: string | number
  title: string
  message: string
  severity: string
  type?: string | null
  isRead?: boolean | null
  createdAt?: string | null
  resolvedAt?: string | null
}

export interface StaffingHeatmapRecordDTO {
  id?: number
  department: string
  timeSlot?: string | null
  dayOfWeek?: string | null
  staffingLevel?: number | null
  demandLevel?: number | null
  status?: string | null
  createdAt?: string | null
}

export interface StaffingHeatmapDTO {
  department: string
  morning: number
  afternoon: number
  night: number
  total: number
}

export interface WeeklyForecastDTO {
  id?: number
  dayOfWeek?: string | null
  predictedDemand?: number | null
  actualDemand?: number | null
  variance?: number | null
  department?: string | null
  weekStartDate?: string | null
  createdAt?: string | null
}

export interface HourlyForecastDTO {
  id?: number
  hour?: string | null
  predictedDemand?: number | null
  confidenceInterval?: number | null
  department?: string | null
  forecastDate?: string | null
  createdAt?: string | null
}

export interface PredictionResultDTO {
  id?: number
  attendanceDate?: string | null
  department?: string | null
  actualDemand?: number | null
  predictedDemand?: number | null
  employeeName?: string | null
  attendenceStatus?: string | null
  capacityUtilization?: string | null
  alertStatus?: string | null
  customerOrders?: string | null
  productivityScore?: string | null
  capacityLoad?: string | null
  scalingEvents?: string | null
}
