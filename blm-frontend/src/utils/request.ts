import axios from 'axios'
import { ElMessage } from 'element-plus'

const service = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api', // 配合 Vite 环境变量
  timeout: 5000
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    // 在这里可以添加 token，例如: config.headers.Authorization = 'Bearer ' + token
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器
service.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    ElMessage.error(error.message || '请求失败')
    return Promise.reject(error)
  }
)

export default service