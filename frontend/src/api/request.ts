import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: '/api/v1',
  timeout: 30000
})

let isRefreshing = false
let refreshSubscribers: ((token: string) => void)[] = []

function onRefreshed(token: string) {
  refreshSubscribers.forEach(cb => cb(token))
  refreshSubscribers = []
}

request.interceptors.request.use(config => {
  const token = localStorage.getItem('accessToken')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  async error => {
    if (error.response?.status === 401 && !error.config._retry) {
      const refreshToken = localStorage.getItem('refreshToken')
      if (refreshToken && !isRefreshing) {
        error.config._retry = true
        isRefreshing = true

        try {
          const res = await axios.post('/api/v1/auth/refresh', { refreshToken })
          const { accessToken, refreshToken: newRefresh } = res.data.data
          localStorage.setItem('accessToken', accessToken)
          localStorage.setItem('refreshToken', newRefresh)
          isRefreshing = false
          onRefreshed(accessToken)

          error.config.headers.Authorization = `Bearer ${accessToken}`
          return request(error.config)
        } catch {
          isRefreshing = false
          refreshSubscribers = []
          localStorage.removeItem('accessToken')
          localStorage.removeItem('refreshToken')
          router.replace('/login')
          return Promise.reject(error)
        }
      }

      if (isRefreshing) {
        return new Promise(resolve => {
          refreshSubscribers.push((token: string) => {
            error.config.headers.Authorization = `Bearer ${token}`
            resolve(request(error.config))
          })
        })
      }

      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      router.replace('/login')
    }

    return Promise.reject(error)
  }
)

export default request
