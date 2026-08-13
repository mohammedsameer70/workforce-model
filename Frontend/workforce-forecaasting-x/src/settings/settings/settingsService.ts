import api from '@/services/apiClient'

export interface SettingsProfileDTO {
  fullName: string
  email: string
  role: string
  department: string
}

export interface SettingsAppearanceDTO {
  darkMode: boolean
  compactView: boolean
  animations: boolean
}

export interface SettingsNotificationDTO {
  criticalAlerts: boolean
  shiftRecommendations: boolean
  systemMonitoring: boolean
  emailDigest: boolean
}

export interface SettingsConfigDTO {
  model: string
  refresh: string
  apiUrl: string
  mlUrl: string
}

export interface SettingsDTO {
  profile: SettingsProfileDTO
  appearance: SettingsAppearanceDTO
  notifications: SettingsNotificationDTO
  config: SettingsConfigDTO
}

class SettingsService {
  async getSettings(): Promise<SettingsDTO> {
    const response = await api.get<SettingsDTO>('/settings')
    return response.data
  }

  async saveSettings(settings: SettingsDTO): Promise<SettingsDTO> {
    const response = await api.put<SettingsDTO>('/settings', settings)
    return response.data
  }
}

export default new SettingsService()
