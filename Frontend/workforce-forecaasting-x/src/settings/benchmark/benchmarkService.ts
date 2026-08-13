import axios from 'axios';

export interface BenchmarkMetricDTO {
  name: string;
  value: number;
  unit: string;
  trend: 'up' | 'down' | 'stable';
}

export interface LatencyPointDTO {
  timestamp: string;
  value: number;
}

export interface VersionHistoryDTO {
  version: string;
  date: string;
  score: number;
}

export interface ExperimentDTO {
  id: string;
  name: string;
  status: 'running' | 'completed' | 'failed';
  startDate: string;
  endDate?: string;
}

const API_BASE_URL = 'http://localhost:5233/api/benchmark';

class BenchmarkService {
  async getMetrics(): Promise<BenchmarkMetricDTO[]> {
    try {
      const response = await axios.get(`${API_BASE_URL}/metrics`);
      return response.data;
    } catch (error) {
      console.error('Failed to fetch metrics:', error);
      return [];
    }
  }

  async getLatencySeries(): Promise<LatencyPointDTO[]> {
    try {
      const response = await axios.get(`${API_BASE_URL}/latency`);
      return response.data;
    } catch (error) {
      console.error('Failed to fetch latency series:', error);
      return [];
    }
  }

  async getVersionHistory(): Promise<VersionHistoryDTO[]> {
    try {
      const response = await axios.get(`${API_BASE_URL}/versions`);
      return response.data;
    } catch (error) {
      console.error('Failed to fetch version history:', error);
      return [];
    }
  }

  async getExperiments(): Promise<ExperimentDTO[]> {
    try {
      const response = await axios.get(`${API_BASE_URL}/experiments`);
      return response.data;
    } catch (error) {
      console.error('Failed to fetch experiments:', error);
      return [];
    }
  }
}

export default new BenchmarkService();
