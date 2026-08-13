import api from '@/services/apiClient'

export interface InfrastructureMetricDTO {
  timestamp: string
  cpu: number
  memory: number
}

export interface LatencyPointDTO {
  timestamp: string
  latency: number
}

export interface MonitoringMetricDTO {
  timestamp: string
  cpu?: number
  memory?: number
  latency?: number
  cpuUsage?: number
  memoryUsage?: number
  responseTime?: number
  [key: string]: any
}

export interface LatencyPointDTO {
  timestamp: string
  latency: number
}

export interface MonitoringMetricDTO {
  timestamp: string
  cpu?: number
  memory?: number
  latency?: number
  responseTime?: number
  [key: string]: any
}

class MonitorService {
  async getInfrastructureMetrics(): Promise<InfrastructureMetricDTO[]> {
    const response = await api.get<MonitoringMetricDTO[]>('/monitor/monitoring-metrics')

    return response.data.map((item) => ({
      timestamp: item.timestamp ?? '',
      cpu:
        typeof item.cpu === 'number'
          ? item.cpu
          : typeof item.cpuUsage === 'number'
          ? item.cpuUsage
          : 0,
      memory:
        typeof item.memory === 'number'
          ? item.memory
          : typeof item.memoryUsage === 'number'
          ? item.memoryUsage
          : 0,
    }))
  }

  async getApiLatency(): Promise<LatencyPointDTO[]> {
    const response = await api.get<MonitoringMetricDTO[]>('/monitor/monitoring-metrics')

    return response.data.map((item) => ({
      timestamp: item.timestamp ?? '',
      latency:
        typeof item.latency === 'number'
          ? item.latency
          : typeof item.responseTime === 'number'
          ? item.responseTime
          : 0,
    }))
  }

}

export default new MonitorService()
