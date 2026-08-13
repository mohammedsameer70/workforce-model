import axios from 'axios';

export interface CapacityMetricDTO {
  department: string;
  currentCapacity: number;
  requiredCapacity: number;
  utilization: number;
  status: 'under' | 'optimal' | 'over';
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
  metrics: CapacityMetricDTO[];
}

export interface CapacityFilterDTO {
  department?: string;
  dateRange: string;
  horizon: number;
}

const API_BASE_URL = 'http://localhost:5233/api/capacity';

class CapacityPlanningService {
  async getCurrentCapacity(filters?: CapacityFilterDTO): Promise<CapacityMetricDTO[]> {
    try {
      const response = await axios.get(`${API_BASE_URL}/current`, { params: filters });
      return response.data;
    } catch (error) {
      console.error('Failed to fetch current capacity:', error);
      return [];
    }
  }

  async getCapacityForecast(filters?: CapacityFilterDTO): Promise<CapacityForecastDTO[]> {
    try {
      const response = await axios.get(`${API_BASE_URL}/forecast`, { params: filters });
      return response.data;
    } catch (error) {
      console.error('Failed to fetch capacity forecast:', error);
      return [];
    }
  }

  async getCapacityPlans(): Promise<CapacityPlanDTO[]> {
    try {
      const response = await axios.get(`${API_BASE_URL}/plans`);
      return response.data;
    } catch (error) {
      console.error('Failed to fetch capacity plans:', error);
      return [];
    }
  }

  async createCapacityPlan(plan: Omit<CapacityPlanDTO, 'id'>): Promise<CapacityPlanDTO> {
    try {
      const response = await axios.post(`${API_BASE_URL}/plans`, plan);
      return response.data;
    } catch (error) {
      console.error('Failed to create capacity plan:', error);
      throw error;
    }
  }

  async updateCapacityPlan(id: string, plan: Partial<CapacityPlanDTO>): Promise<CapacityPlanDTO> {
    try {
      const response = await axios.put(`${API_BASE_URL}/plans/${id}`, plan);
      return response.data;
    } catch (error) {
      console.error('Failed to update capacity plan:', error);
      throw error;
    }
  }

  async deleteCapacityPlan(id: string): Promise<void> {
    try {
      await axios.delete(`${API_BASE_URL}/plans/${id}`);
    } catch (error) {
      console.error('Failed to delete capacity plan:', error);
      throw error;
    }
  }

  async optimizeCapacity(filters?: CapacityFilterDTO): Promise<any> {
    try {
      const response = await axios.post(`${API_BASE_URL}/optimize`, { filters });
      return response.data;
    } catch (error) {
      console.error('Failed to optimize capacity:', error);
      throw error;
    }
  }
}

export default new CapacityPlanningService();
