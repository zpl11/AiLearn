import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router' // 引入你的路由实例，用于 401 时自动跳转登录页

// 1. 创建 Axios 实例
const service = axios.create({
    baseURL: 'http://localhost:8080', // 后端 Spring Boot 服务的地址
    timeout: 5000,
})

// 2. 请求拦截器：统一在 Header 中携带 Token
service.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('TOKEN')
        if (token) {
            // 遵循工业级 Bearer Token 标准传输格式
            config.headers['Authorization'] = `Bearer ${token}`
        }
        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

// 3. 响应拦截器：统一剥离后端的 Result 外壳与鉴权失效处理
service.interceptors.response.use(
    (response) => {
        const res = response.data // 后端的 Result 对象: { code, msg/message, data }

        // 兼容后端的提示字段（msg 或 message）
        const errorMsg = res.msg || res.message || '系统错误'

        // 场景 A：后端业务状态码为 401（未登录或凭据已过期）
        if (res.code === 401) {
            ElMessage.error('登录状态已过期，请重新登录')
            // 清理本地失效凭据
            localStorage.removeItem('TOKEN')
            localStorage.removeItem('USER_INFO')
            localStorage.removeItem('ROLES')
            localStorage.removeItem('PERMISSIONS')
            // 踢回登录页
            router.push('/login')
            return Promise.reject(new Error(errorMsg))
        }

        // 场景 B：其他业务逻辑失败（如状态码非 200）
        if (res.code !== 200) {
            ElMessage.error(errorMsg)
            return Promise.reject(new Error(errorMsg))
        }

        // 成功时直接把内部的 data 吐给前端业务页面
        return res.data
    },
    (error) => {
        // 处理 HTTP 网络协议层面的错误
        if (error.response) {
            const status = error.response.status
            if (status === 401) {
                ElMessage.error('认证失败或登录已过期，请重新登录')
                localStorage.clear()
                router.push('/login')
            } else if (status === 403) {
                ElMessage.error('权限不足，无法访问该资源')
            } else if (status === 500) {
                ElMessage.error('服务端内部错误，请联系系统管理员')
            } else {
                ElMessage.error(error.response.data?.msg || error.response.data?.message || '网络请求失败')
            }
        } else {
            // 处理断网或超时
            ElMessage.error(error.message || '网络连接异常')
        }
        return Promise.reject(error)
    }
)

export default service