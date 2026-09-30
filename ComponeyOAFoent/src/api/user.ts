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

// 用户角色绑定入参载荷契约（对齐后端 UserRoleDTO）
export interface UserRolePayload {
    userId: number | string
    roleIds: (number | string)[]
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

// ==================== 增补：用户与角色关联相关接口 ====================

/**
 * 根据用户 ID 查询已分配的角色 ID 列表（用于弹窗回显勾选）
 */
export function getRoleIdsByUser(userId: number | string) {
    return request({
        url: `/system/user/${userId}/roleIds`,
        method: 'get'
    })
}

/**
 * 为员工分配角色（提交保存，对齐后端 /system/user/authRole）
 */
export function assignUserRoles(data: UserRolePayload) {
    return request({
        url: '/system/user/authRole',
        method: 'put',
        data
    })
}