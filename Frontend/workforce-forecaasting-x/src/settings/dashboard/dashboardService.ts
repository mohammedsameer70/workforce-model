import api from '@/services/apiClient'
import type {
  AlertDTO,
  DashboardDataDTO,
  DashboardMetricDTO,
  DepartmentPerformanceDTO,
  HourlyForecastDTO,
  PredictionResultDTO,
  PredictionTrendDTO,
  RecentPredictionDTO,
  StaffingHeatmapDTO,
  StaffingHeatmapRecordDTO,
  WeeklyForecastDTO,
} from './dashboardDTO'

const formatMetricValue = (value: number | null | undefined) => {
  if (value === null || value === undefined || Number.isNaN(value)) {
    return '0'
  }

  return Number(value).toLocaleString(undefined, {
    maximumFractionDigits: 2,
  })
}

class CLDashboardService {
  async getKpiCards(category = 'dashboard'): Promise<DashboardMetricDTO[]> {
    const response = await api.get<DashboardMetricDTO[]>(`/kpi-cards/category/${category}`)

    if (response.data && response.data.length > 0) {
      return response.data
    }

    const predictionsResponse = await api.get<PredictionResultDTO[]>('/dashboard/predictions')
    const predictions = predictionsResponse.data ?? []

    const predictedValues = predictions
      .map((item) => (typeof item.predictedDemand === 'number' ? item.predictedDemand : null))
      .filter((value): value is number => value !== null)

    return [
      {
        title: 'Total Predictions',
        value: predictions.length.toString(),
      },
      {
        title: 'Avg Predicted Demand',
        value: formatMetricValue(predictedValues.reduce((sum, value) => sum + value, 0) / (predictedValues.length || 1)),
      },
      {
        title: 'Max Predicted Demand',
        value: formatMetricValue(predictedValues.length > 0 ? Math.max(...predictedValues) : 0),
      },
      {
        title: 'Min Predicted Demand',
        value: formatMetricValue(predictedValues.length > 0 ? Math.min(...predictedValues) : 0),
      },
    ]
  }

  async getDashboardData(): Promise<DashboardDataDTO> {
    const response = await api.get<DashboardDataDTO>('/dashboard')
    return response.data
  }

  async getAlerts(limit = 20): Promise<AlertDTO[]> {
    const response = await api.get<AlertDTO[]>('/alerts', {
      params: {
        limit,
      },
    })
    return response.data
  }

  async getStaffingHeatmap(): Promise<StaffingHeatmapDTO[]> {
    const response = await api.get<
      StaffingHeatmapDTO[] | StaffingHeatmapRecordDTO[] | { value: StaffingHeatmapDTO[] | StaffingHeatmapRecordDTO[]; Count?: number }
    >('/forecasting/staffing-heatmap')

    const rawData = Array.isArray(response.data)
      ? response.data
      : Array.isArray((response.data as { value?: unknown }).value)
      ? (response.data as { value: StaffingHeatmapDTO[] | StaffingHeatmapRecordDTO[] }).value
      : []

    if (rawData.length === 0) {
      return []
    }

    const firstItem = rawData[0] as Record<string, unknown>
    if (typeof firstItem.morning === 'number' || typeof firstItem.afternoon === 'number' || typeof firstItem.night === 'number') {
      return rawData as StaffingHeatmapDTO[]
    }

    const grouped = new Map<string, StaffingHeatmapDTO>()

    ;(rawData as StaffingHeatmapRecordDTO[]).forEach((item) => {
      const department = item.department ?? 'Unknown'
      const entry = grouped.get(department) ?? { department, morning: 0, afternoon: 0, night: 0, total: 0 }
      const value = typeof item.staffingLevel === 'number' ? item.staffingLevel : 0
      const slot = (item.timeSlot ?? '').toLowerCase()

      if (slot.includes('morning')) {
        entry.morning += value
      } else if (slot.includes('afternoon')) {
        entry.afternoon += value
      } else if (slot.includes('night')) {
        entry.night += value
      }

      entry.total += value
      grouped.set(department, entry)
    })

    return Array.from(grouped.values()).sort((a, b) => a.department.localeCompare(b.department))
  }

  async getDepartmentPerformance(): Promise<DepartmentPerformanceDTO[]> {
    const response = await api.get<WeeklyForecastDTO[]>('/forecasting/weekly')

    if (response.data && response.data.length > 0) {
      const grouped = new Map<string, number[]>()

      response.data.forEach((item) => {
        const department = item.department ?? 'Unknown'
        const parsedValue = typeof item.predictedDemand === 'number' ? item.predictedDemand : 0
        const values = grouped.get(department) ?? []
        values.push(parsedValue)
        grouped.set(department, values)
      })

      return Array.from(grouped.entries())
        .map(([department, values]) => ({
          department,
          averagePrediction: values.reduce((sum, value) => sum + value, 0) / (values.length || 1),
        }))
        .sort((a, b) => a.department.localeCompare(b.department))
    }

    const predictionsResponse = await api.get<PredictionResultDTO[]>('/dashboard/predictions')
    const grouped = new Map<string, number[]>()

    predictionsResponse.data.forEach((item) => {
      const department = item.department ?? 'Unknown'
      const values = grouped.get(department) ?? []
      if (typeof item.predictedDemand === 'number') {
        values.push(item.predictedDemand)
      }
      grouped.set(department, values)
    })

    return Array.from(grouped.entries())
      .map(([department, values]) => ({
        department,
        averagePrediction: values.reduce((sum, value) => sum + value, 0) / (values.length || 1),
      }))
      .sort((a, b) => a.department.localeCompare(b.department))
  }

  async getTrendForecast(): Promise<PredictionTrendDTO[]> {
    const response = await api.get<HourlyForecastDTO[]>('/forecasting/hourly')

    if (response.data && response.data.length > 0) {
      return response.data
        .map((item) => ({
          date: item.forecastDate ?? item.hour ?? 'Unknown',
          predictedDemand: typeof item.predictedDemand === 'number' ? item.predictedDemand : 0,
          actualDemand: null,
        }))
        .sort((a, b) => a.date.localeCompare(b.date))
    }

    const predictionsResponse = await api.get<PredictionResultDTO[]>('/dashboard/predictions')
    const grouped = new Map<string, { predictedDemand: number; actualDemand: number | null }>()

    predictionsResponse.data.forEach((item) => {
      const date = item.attendanceDate ?? 'Unknown'
      const previous = grouped.get(date) ?? { predictedDemand: 0, actualDemand: null }
      if (typeof item.predictedDemand === 'number') {
        previous.predictedDemand += item.predictedDemand
      }
      if (typeof item.actualDemand === 'number') {
        previous.actualDemand = (previous.actualDemand ?? 0) + item.actualDemand
      }
      grouped.set(date, previous)
    })

    return Array.from(grouped.entries())
      .map(([date, values]) => ({
        date,
        predictedDemand: values.predictedDemand,
        actualDemand: values.actualDemand,
      }))
      .sort((a, b) => a.date.localeCompare(b.date))
  }

  async getRecentPredictions(): Promise<RecentPredictionDTO[]> {
    const response = await api.get<PredictionResultDTO[]>('/dashboard/predictions')

    return response.data.map((item) => ({
      id: item.id ?? 0,
      date: item.attendanceDate ?? null,
      department: item.department ?? 'Unknown',
      actualDemand: typeof item.actualDemand === 'number' ? item.actualDemand : 0,
      predictedDemand: typeof item.predictedDemand === 'number' ? item.predictedDemand : 0,
    }))
  }
}

export default new CLDashboardService();