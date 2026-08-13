import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:5233/api',
  timeout: 15000,
})

export default api
