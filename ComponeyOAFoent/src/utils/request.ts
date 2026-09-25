import axios from 'axios'
import { ElMessage } from 'element-plus'

// 1. 创建 Axios 实例
const service = axios.create({
    baseURL: 'http://localhost:8080', // 后端 Spring Boot 服务的地址
    timeout: 5000,
})

// 2. 响应拦截器：统一剥离后端的 Result 外壳
service.interceptors.response.use(
    (response) => {
        const res = response.data // 这是后端的 Result 对象: { code, message, data }

        // 如果后端的业务状态码不是 200，说明业务校验失败（例如：存在子部门不允许删除）
        if (res.code !== 200) {
            ElMessage.error(res.message || '系统错误')
            return Promise.reject(new Error(res.message || 'Error'))
        }

        // 成功时直接把内部的 data 吐给前端业务页面，省去每处都写 .data.data
        return res.data
    },
    (error) => {
        // 处理 HTTP 网络层面错误（如 500 服务器崩溃、网络断开等）
        ElMessage.error(error.message || '网络连接异常')
        return Promise.reject(error)
    }
)

export default service