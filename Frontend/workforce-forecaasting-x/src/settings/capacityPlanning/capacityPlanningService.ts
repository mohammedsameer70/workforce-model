import api from '@/services/apiClient';

export interface CapacityMetricDTO {
  title: string;
  value: string;
  icon?: string;
}

export interface DepartmentCapacityDTO {
  name: string;
  utilization: number;
  status: string;
}

export interface TimeSeriesDTO {
  label: string;
  utilization: number;
  capacity: number;
}

export interface BenchmarkPointDTO {
  label: string;
  value: number;
  target: number;
}

export interface CapacityForecastDTO {
  date: string;
  predictedDemand: number;
  recommendedCapacity: number;
  confidence: number;
}

export interface CapacityPlanDTO {
  id: string;
  name: string;
  department: string;
  startDate: string;
  endDate: string;
  status: 'draft' | 'active' | 'completed';
  metrics: any[];
}

export interface CapacityFilterDTO {
  department?: string;
  dateRange: string;
  horizon: number;
}

class CapacityPlanningService {
  async getMetrics(): Promise<CapacityMetricDTO[]> {
    try {
      const response = await api.get<CapacityMetricDTO[]>('/capacity/metrics');
      return response.data;
    } catch (error) {
      console.error('Failed to fetch capacity metrics:', error);
      return [];
    }
  }

  async getCapacityTrend(): Promise<TimeSeriesDTO[]> {
    try {
      const response = await api.get<TimeSeriesDTO[]>('/capacity/trend');
      return response.data;
    } catch (error) {
      console.error('Failed to fetch capacity trend:', error);
      return [];
    }
  }

  async getDepartments(): Promise<string[]> {
    try {
      const response = await api.get<string[]>('/capacity/departments');
      return response.data;
    } catch (error) {
      console.error('Failed to fetch departments:', error);
      return [];
    }
  }

  async getBenchmark(): Promise<BenchmarkPointDTO[]> {
    try {
      const response = await api.get<BenchmarkPointDTO[]>('/capacity/benchmark');
      return response.data;
    } catch (error) {
      console.error('Failed to fetch benchmark:', error);
      return [];
    }
  }

  async getCurrentCapacity(filters?: CapacityFilterDTO): Promise<any[]> {
    try {
      const response = await api.get('/capacity/current', { params: filters });
      return response.data;
    } catch (error) {
      console.error('Failed to fetch current capacity:', error);
      return [];
    }
  }

  async getCapacityForecast(filters?: CapacityFilterDTO): Promise<CapacityForecastDTO[]> {
    try {
      const response = await api.get('/capacity/forecast', { params: filters });
      return response.data;
    } catch (error) {
      console.error('Failed to fetch capacity forecast:', error);
      return [];
    }
  }

  async getCapacityPlans(): Promise<CapacityPlanDTO[]> {
    try {
      const response = await api.get('/capacity/plans');
      return response.data;
    } catch (error) {
      console.error('Failed to fetch capacity plans:', error);
      return [];
    }
  }

  async createCapacityPlan(plan: Omit<CapacityPlanDTO, 'id'>): Promise<CapacityPlanDTO> {
    try {
      const response = await api.post('/capacity/plans', plan);
      return response.data;
    } catch (error) {
      console.error('Failed to create capacity plan:', error);
      throw error;
    }
  }

  async updateCapacityPlan(id: string, plan: Partial<CapacityPlanDTO>): Promise<CapacityPlanDTO> {
    try {
      const response = await api.put(`/capacity/plans/${id}`, plan);
      return response.data;
    } catch (error) {
      console.error('Failed to update capacity plan:', error);
      throw error;
    }
  }

  async deleteCapacityPlan(id: string): Promise<void> {
    try {
      await api.delete(`/capacity/plans/${id}`);
    } catch (error) {
      console.error('Failed to delete capacity plan:', error);
      throw error;
    }
  }

  async optimizeCapacity(filters?: CapacityFilterDTO): Promise<any> {
    try {
      const response = await api.post('/capacity/optimize', { filters });
      return response.data;
    } catch (error) {
      console.error('Failed to optimize capacity:', error);
      throw error;
    }
  }
}

export default new CapacityPlanningService();
