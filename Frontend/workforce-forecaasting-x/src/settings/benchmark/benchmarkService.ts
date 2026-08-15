import api from '@/services/apiClient';

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

class BenchmarkService {
  async getMetrics(): Promise<BenchmarkMetricDTO[]> {
    try {
      const response = await api.get<BenchmarkMetricDTO[]>('/benchmark/metrics');
      return response.data;
    } catch (error) {
      console.error('Failed to fetch metrics:', error);
      return [];
    }
  }

  async getLatencySeries(): Promise<LatencyPointDTO[]> {
    try {
      const response = await api.get<LatencyPointDTO[]>('/benchmark/latency');
      return response.data;
    } catch (error) {
      console.error('Failed to fetch latency series:', error);
      return [];
    }
  }

  async getVersionHistory(): Promise<VersionHistoryDTO[]> {
    try {
      const response = await api.get<VersionHistoryDTO[]>('/benchmark/versions');
      return response.data;
    } catch (error) {
      console.error('Failed to fetch version history:', error);
      return [];
    }
  }

  async getExperiments(): Promise<ExperimentDTO[]> {
    try {
      const response = await api.get<ExperimentDTO[]>('/benchmark/experiments');
      return response.data;
    } catch (error) {
      console.error('Failed to fetch experiments:', error);
      return [];
    }
  }
}

export default new BenchmarkService();
