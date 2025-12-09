import axios from 'axios'
import { useUserStore } from '../stores/user'
import router from '../router'
import { ElMessage } from 'element-plus'

// 1. 创建 Axios 实例
const service = axios.create({
    baseURL: 'http://localhost:8080', // 后端服务的地址
    timeout: 10000, // 请求超时时间
    // 登录接口使用 form-urlencoded 格式，其他接口默认使用 JSON
    headers: {
        'Content-Type': 'application/json'
    }
})

// 2. 请求拦截器：自动携带 Token
service.interceptors.request.use(
    (config) => {
        const userStore = useUserStore()
        const token = userStore.token

        if (token) {
            // 携带 JWT Token，格式为 Authorization: Bearer <Token>
            config.headers['Authorization'] = `Bearer ${token}`
        }
        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

// 3. 响应拦截器：处理错误和权限问题
service.interceptors.response.use(
    (response) => {
        // 后端约定的成功状态码 (例如 200)
        if (response.data.code === 200) {
            return response.data
        } else {
            // 业务错误
            ElMessage.error(response.data.msg || '业务请求失败')
            return Promise.reject(new Error(response.data.msg || 'Error'))
        }
    },
    (error) => {
        const userStore = useUserStore()
        const status = error.response.status

        // 🚨 关键修复：检查 error.response 是否存在
        if (error.response) {
            const status = error.response.status

            if (status === 401) {
                userStore.clearToken()
                router.push('/login')
                ElMessage.error('登录状态失效，请重新登录！')
            } else if (status === 403) {
                ElMessage.error('权限不足，无法执行此操作！')
            } else {
                // 兜底错误提示
                ElMessage.error(error.message || '服务器内部错误！')
            }
        } else {
            // 如果 error.response 不存在 (例如网络断开、CORS 拦截)
            ElMessage.error('网络连接错误或请求被阻止，请检查后端服务！')
        }

        return Promise.reject(error)
    }
)

export default service