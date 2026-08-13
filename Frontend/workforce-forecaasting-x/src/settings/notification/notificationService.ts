import api from '@/services/apiClient'

export interface NotificationDTO {
  id: string | number
  title: string
  message: string
  type: string
  unread: boolean
  time: string
  icon: string
}

export interface NotificationItemDTO {
  id: string | number
  title: string
  message: string
  type: string
  unread: boolean
  time: string
  icon: string
}

class NotificationService {
  async getNotifications(): Promise<NotificationDTO[]> {
    const response = await api.get<NotificationDTO[]>('/notifications')
    return response.data
  }
}

export default new NotificationService()
