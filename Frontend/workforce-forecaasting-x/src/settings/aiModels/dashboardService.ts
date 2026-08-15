import axios from 'axios';

export interface DashboardMetricDTO {
  title: string;
  value: string | number;
  icon: string;
  trend?: 'up' | 'down' | 'stable';
  change?: number;
}

export interface DashboardChartDataDTO {
  labels: string[];
  datasets: {
    label: string;
    data: number[];
    backgroundColor?: string;
    borderColor?: string;
  }[];
}

export interface DashboardStatsDTO {
  totalEmployees: number;
  activeShifts: number;
  departments: number;
  predictions: number;
  modelAccuracy: number;
}

const API_BASE_URL = '/api/dashboard';

class DashboardService {
  async getDashboardData(): Promise<{
    metrics?: DashboardMetricDTO[];
    stats?: DashboardStatsDTO;
    charts?: {
      workforceTrend: DashboardChartDataDTO;
      departmentDistribution: DashboardChartDataDTO;
      predictionAccuracy: DashboardChartDataDTO;
    };
  }> {
    try {
      const response = await axios.get(`${API_BASE_URL}/overview`);
      return response.data;
    } catch (error) {
      console.error('Failed to fetch dashboard data:', error);
      return {};
    }
  }

  async getWorkforceTrend(period: string = '7d'): Promise<DashboardChartDataDTO> {
    try {
      const response = await axios.get(`${API_BASE_URL}/workforce-trend`, { params: { period } });
      return response.data;
    } catch (error) {
      console.error('Failed to fetch workforce trend:', error);
      return { labels: [], datasets: [] };
    }
  }

  async getDepartmentDistribution(): Promise<DashboardChartDataDTO> {
    try {
      const response = await axios.get(`${API_BASE_URL}/department-distribution`);
      return response.data;
    } catch (error) {
      console.error('Failed to fetch department distribution:', error);
      return { labels: [], datasets: [] };
    }
  }

  async getPredictionAccuracy(): Promise<DashboardChartDataDTO> {
    try {
      const response = await axios.get(`${API_BASE_URL}/prediction-accuracy`);
      return response.data;
    } catch (error) {
      console.error('Failed to fetch prediction accuracy:', error);
      return { labels: [], datasets: [] };
    }
  }

  async getRecentActivities(limit: number = 10): Promise<any[]> {
    try {
      const response = await axios.get(`${API_BASE_URL}/activities`, { params: { limit } });
      return response.data;
    } catch (error) {
      console.error('Failed to fetch recent activities:', error);
      return [];
    }
  }
}

export default new DashboardService();
