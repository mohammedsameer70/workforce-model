import api from '@/services/apiClient';

export interface AnalyticsMetricDTO {
  title: string;
  value: string;
  icon?: string;
}

export interface TimeSeriesPointDTO {
  label: string;
  value: number;
}

export interface DepartmentDistributionDTO {
  department: string;
  value: number;
}

export interface AnalyticsFilterDTO {
  dateRange: string;
  department?: string;
  team?: string;
}

class AnalyticsService {
  async getMetrics(filters?: AnalyticsFilterDTO): Promise<AnalyticsMetricDTO[]> {
    try {
      const response = await api.get<AnalyticsMetricDTO[]>('/analytics/metrics', { params: filters });
      return response.data;
    } catch (error) {
      console.error('Failed to fetch analytics metrics:', error);
      return [];
    }
  }

  async getHourlyThroughput(filters?: AnalyticsFilterDTO): Promise<TimeSeriesPointDTO[]> {
    try {
      const response = await api.get<TimeSeriesPointDTO[]>('/analytics/hourly-throughput', { params: filters });
      return response.data;
    } catch (error) {
      console.error('Failed to fetch hourly throughput:', error);
      return [];
    }
  }

  async getDemandForecast(filters?: AnalyticsFilterDTO): Promise<TimeSeriesPointDTO[]> {
    try {
      const response = await api.get<TimeSeriesPointDTO[]>('/analytics/demand-forecast', { params: filters });
      return response.data;
    } catch (error) {
      console.error('Failed to fetch demand forecast:', error);
      return [];
    }
  }

  async getDepartmentDistribution(filters?: AnalyticsFilterDTO): Promise<DepartmentDistributionDTO[]> {
    try {
      const response = await api.get<DepartmentDistributionDTO[]>('/analytics/department-distribution', { params: filters });
      return response.data;
    } catch (error) {
      console.error('Failed to fetch department distribution:', error);
      return [];
    }
  }

  async getWeeklyComparison(filters?: AnalyticsFilterDTO): Promise<TimeSeriesPointDTO[]> {
    try {
      const response = await api.get<TimeSeriesPointDTO[]>('/analytics/weekly-comparison', { params: filters });
      return response.data;
    } catch (error) {
      console.error('Failed to fetch weekly comparison:', error);
      return [];
    }
  }

  async getTrendData(metric: string, filters?: AnalyticsFilterDTO): Promise<any[]> {
    try {
      const response = await api.get(`/analytics/trends/${metric}`, { params: filters });
      return response.data;
    } catch (error) {
      console.error('Failed to fetch trend data:', error);
      return [];
    }
  }

  async getDepartmentPerformance(filters?: AnalyticsFilterDTO): Promise<any[]> {
    try {
      const response = await api.get('/analytics/departments', { params: filters });
      return response.data;
    } catch (error) {
      console.error('Failed to fetch department performance:', error);
      return [];
    }
  }

  async getTeamPerformance(filters?: AnalyticsFilterDTO): Promise<any[]> {
    try {
      const response = await api.get('/analytics/teams', { params: filters });
      return response.data;
    } catch (error) {
      console.error('Failed to fetch team performance:', error);
      return [];
    }
  }

  async exportAnalytics(filters?: AnalyticsFilterDTO): Promise<Blob> {
    try {
      const response = await api.get('/analytics/export', { 
        params: filters,
        responseType: 'blob'
      });
      return response.data;
    } catch (error) {
      console.error('Failed to export analytics:', error);
      throw error;
    }
  }
}

export default new AnalyticsService();
