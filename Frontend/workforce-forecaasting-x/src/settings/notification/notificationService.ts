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

interface BackendNotificationDTO {
  id: number
  title: string
  message: string
  type: string
  priority: string
  isRead: boolean
  icon: string
  createdAt: string
  readAt?: string
}

const formatTime = (dateString: string) => {
  const date = new Date(dateString)
  const now = new Date()
  const diffMs = now.getTime() - date.getTime()
  const diffMins = Math.floor(diffMs / 60000)
  const diffHours = Math.floor(diffMs / 3600000)
  const diffDays = Math.floor(diffMs / 86400000)

  if (diffMins < 1) return 'Just now'
  if (diffMins < 60) return `${diffMins}m ago`
  if (diffHours < 24) return `${diffHours}h ago`
  if (diffDays < 7) return `${diffDays}d ago`
  return date.toLocaleDateString()
}

class NotificationService {
  async getNotifications(): Promise<NotificationDTO[]> {
    const response = await api.get<BackendNotificationDTO[]>('/notifications')
    return response.data.map((item) => ({
      id: item.id,
      title: item.title,
      message: item.message,
      type: item.type?.toLowerCase() || 'info',
      unread: !item.isRead,
      time: formatTime(item.createdAt),
      icon: item.icon || 'pi pi-info-circle',
    }))
  }

  async markAsRead(id: number): Promise<void> {
    await api.patch(`/notifications/${id}/mark-read`)
  }

  async markAllAsRead(): Promise<void> {
    const notifications = await this.getNotifications()
    const unreadIds = notifications.filter((n) => n.unread).map((n) => Number(n.id))
    
    await Promise.all(unreadIds.map((id) => this.markAsRead(id)))
  }
}

export default new NotificationService()
