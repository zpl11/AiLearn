import request from '@/utils/request'
import type { SysDept } from '@/api/dept'

export interface SysUser {
    userId?: number | string
    deptId?: number | string
    userName: string
    nickName: string
    password?: string
    phone?: string
    email?: string
    sex?: string
    status?: number
    delFlag?: number
    createTime?: string
}

export function getUserList() {
    return request({ url: '/system/user/list', method: 'get' })
}

export function getUserByDept(deptId: number | string) {
    return request({ url: '/system/user/byDept', method: 'get', params: { deptId } })
}

export function addUser(data: SysUser) {
    return request({ url: '/system/user/add', method: 'post', data })
}

export function updateUser(data: SysUser) {
    return request({ url: '/system/user/update', method: 'put', data })
}

export function deleteUser(userId: number | string) {
    return request({ url: '/system/user/del', method: 'delete', params: { userId } })
}