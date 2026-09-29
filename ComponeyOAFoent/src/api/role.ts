import request from '@/utils/request'

export interface SysRole {
    roleId?: number
    roleName: string
    roleCode: string
    roleSort: number
    status: number
    remark?: string
    createTime?: string
}

// 查询角色列表
export function getRoleList(params?: { roleName?: string; status?: number | string }) {
    return request<SysRole[]>({
        url: '/system/role/list',
        method: 'get',
        params
    })
}

// 获取角色分配的菜单 ID 集合
export function getRoleMenuIds(roleId: number) {
    return request<number[]>({
        url: `/system/role/${roleId}/menuIds`,
        method: 'get'
    })
}

// 新增角色
export function addRole(data: SysRole) {
    return request({
        url: '/system/role',
        method: 'post',
        data
    })
}

// 修改角色
export function updateRole(data: SysRole) {
    return request({
        url: '/system/role',
        method: 'put',
        data
    })
}

// 修改角色状态
export function changeRoleStatus(roleId: number, status: number) {
    return request({
        url: '/system/role/changeStatus',
        method: 'put',
        data: { roleId, status }
    })
}

// 批量/单个删除角色
export function deleteRole(roleIds: number | number[]) {
    const ids = Array.isArray(roleIds) ? roleIds.join(',') : roleIds
    return request({
        url: `/system/role/${ids}`,
        method: 'delete'
    })
}

// 分配权限
export function assignRoleMenus(roleId: number, menuIds: number[]) {
    return request({
        url: `/system/role/${roleId}/menus`,
        method: 'put',
        data: menuIds
    })
}