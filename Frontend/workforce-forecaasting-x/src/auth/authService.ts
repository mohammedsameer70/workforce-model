import api from '@/services/apiClient'

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  token: string
  username: string
  role: string
}

class AuthService {
  async login(username: string, password: string): Promise<LoginResponse> {
    const response = await api.post<LoginResponse>('/auth/login', {
      username,
      password
    })
    return response.data
  }

  async validateToken(token: string): Promise<LoginResponse> {
    const response = await api.get<LoginResponse>('/auth/validate', {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    return response.data
  }

  logout() {
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('role')
    localStorage.removeItem('rememberMe')
  }

  isAuthenticated(): boolean {
    return !!localStorage.getItem('token')
  }

  getCurrentUser(): { username: string | null, role: string | null } {
    return {
      username: localStorage.getItem('username'),
      role: localStorage.getItem('role')
    }
  }

  getToken(): string | null {
    return localStorage.getItem('token')
  }
}

export default new AuthService()
