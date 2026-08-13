import api from '@/services/apiClient'

export interface ForecastMetricDTO {
  title: string
  value: string
  icon?: string
}

export interface ForecastTrendDTO {
  date: string
  predictedDemand: number
  actualDemand?: number
}

export interface WeeklyForecastDTO {
  id?: number
  dayOfWeek?: string | null
  predictedDemand: number
  actualDemand?: number | null
  variance?: number | null
  department: string
  weekStartDate?: string | null
}

export interface StaffingHeatmapDTO {
  department: string
  morning: number
  afternoon: number
  night: number
  total: number
}

class ForecastingService {
  async getForecastMetrics(): Promise<ForecastMetricDTO[]> {
    const response = await api.get<ForecastMetricDTO[]>('/forecasting/radar')
    return response.data
  }

  async getHourlyForecast(): Promise<ForecastTrendDTO[]> {
    const response = await api.get<ForecastTrendDTO[]>('/forecasting/hourly')
    return response.data
  }

  async getWeeklyPerformance(): Promise<WeeklyForecastDTO[]> {
    const response = await api.get<WeeklyForecastDTO[]>('/forecasting/weekly')
    return response.data
  }

  async getStaffingHeatmap(): Promise<StaffingHeatmapDTO[]> {
    const response = await api.get<
      StaffingHeatmapDTO[] | { value: StaffingHeatmapDTO[]; Count?: number }
    >('/forecasting/staffing-heatmap')
    return Array.isArray(response.data)
      ? response.data
      : Array.isArray((response.data as { value?: unknown }).value)
      ? (response.data as { value: StaffingHeatmapDTO[] }).value
      : []
  }

}

export default new ForecastingService()
