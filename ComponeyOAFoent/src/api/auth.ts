import request from '@/utils/request' // 替换为你项目中的 axios 实例路径

// 登录请求载荷 (与后端 LoginDTO 对应)
export interface LoginDTO {
    userName: string
    password: string
}

// 登录响应实体 (与后端 LoginVO 对应)
export interface LoginVO {
    token: string
    userId: number
    userName: string
    nickName: string
    permissions: string[]
    roles: string[]
}

/**
 * 用户登录认证
 */
export const login = (data: LoginDTO): Promise<LoginVO> => {
    return request.post('/auth/login', data)
}