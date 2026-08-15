import api from '@/services/apiClient'

export interface ReportDTO {
  id: string
  report: string
  type: string
  generated: string
  size: string
  status: string
}

export interface ReportMetricDTO {
  value: string
  title: string
  icon: string
}

interface ReportBackendDTO {
  id: number
  name: string
  type: string
  description: string
  generatedAt: string
  fileSize: number
  filePath: string
  status: string
  generatedBy: string
}

const formatFileSize = (bytes: number) => {
  if (bytes >= 1_000_000) {
    return `${(bytes / 1_000_000).toFixed(1)} MB`
  }
  if (bytes >= 1_000) {
    return `${(bytes / 1_000).toFixed(1)} KB`
  }
  return `${bytes} B`
}

class ReportsService {
  async getReportMetrics(): Promise<ReportMetricDTO[]> {
    const response = await api.get<ReportBackendDTO[]>('/reports')
    const reports = response.data
    const total = reports.length
    const ready = reports.filter((item) => item.status?.toLowerCase() === 'ready').length
    const reportTypes = Array.from(new Set(reports.map((item) => item.type).filter(Boolean)))

    return [
      {
        title: 'Total Reports',
        value: total.toString(),
        icon: 'pi pi-folder-open',
      },
      {
        title: 'Ready to Export',
        value: ready.toString(),
        icon: 'pi pi-download',
      },
      {
        title: 'Report Types',
        value: reportTypes.length.toString(),
        icon: 'pi pi-tags',
      },
      {
        title: 'Avg File Size',
        value:
          reports.length > 0
            ? formatFileSize(reports.reduce((sum, item) => sum + (item.fileSize ?? 0), 0) / reports.length)
            : '0 B',
        icon: 'pi pi-chart-bar',
      },
    ]
  }

  async getReports(): Promise<ReportDTO[]> {
    const response = await api.get<ReportBackendDTO[]>('/reports')

    return response.data.map((item) => ({
      id: item.id.toString(),
      report: item.name ?? 'Unnamed Report',
      type: item.type ?? 'Unknown',
      generated: item.generatedAt ?? 'Unknown',
      size: formatFileSize(item.fileSize ?? 0),
      status: item.status ?? 'Pending',
    }))
  }

  async generateReport(type: string): Promise<ReportDTO> {
    const response = await api.post<ReportBackendDTO>('/reports/generate', null, {
      params: { type }
    })

    return {
      id: response.data.id.toString(),
      report: response.data.name ?? 'Unnamed Report',
      type: response.data.type ?? 'Unknown',
      generated: response.data.generatedAt ?? 'Unknown',
      size: formatFileSize(response.data.fileSize ?? 0),
      status: response.data.status ?? 'Pending',
    }
  }
}

export default new ReportsService()
