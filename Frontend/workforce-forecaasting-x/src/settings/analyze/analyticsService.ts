import axios from 'axios';

export interface AnalyticsMetricDTO {
  name: string;
  value: number;
  change: number;
  period: string;
}

export interface TrendDataDTO {
  date: string;
  value: number;
  category?: string;
}

export interface AnalyticsFilterDTO {
  dateRange: string;
  department?: string;
  team?: string;
}

const API_BASE_URL = 'http://localhost:5233/api/analytics';

class AnalyticsService {
  async getMetrics(filters?: AnalyticsFilterDTO): Promise<AnalyticsMetricDTO[]> {
    try {
      const response = await axios.get(`${API_BASE_URL}/metrics`, { params: filters });
      return response.data;
    } catch (error) {
      console.error('Failed to fetch analytics metrics:', error);
      return [];
    }
  }

  async getTrendData(metric: string, filters?: AnalyticsFilterDTO): Promise<TrendDataDTO[]> {
    try {
      const response = await axios.get(`${API_BASE_URL}/trends/${metric}`, { params: filters });
      return response.data;
    } catch (error) {
      console.error('Failed to fetch trend data:', error);
      return [];
    }
  }

  async getDepartmentPerformance(filters?: AnalyticsFilterDTO): Promise<any[]> {
    try {
      const response = await axios.get(`${API_BASE_URL}/departments`, { params: filters });
      return response.data;
    } catch (error) {
      console.error('Failed to fetch department performance:', error);
      return [];
    }
  }

  async getTeamPerformance(filters?: AnalyticsFilterDTO): Promise<any[]> {
    try {
      const response = await axios.get(`${API_BASE_URL}/teams`, { params: filters });
      return response.data;
    } catch (error) {
      console.error('Failed to fetch team performance:', error);
      return [];
    }
  }

  async exportAnalytics(filters?: AnalyticsFilterDTO): Promise<Blob> {
    try {
      const response = await axios.get(`${API_BASE_URL}/export`, { 
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
