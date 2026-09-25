import request from '@/utils/request'

// 定义后端实体在前端的 TypeScript 类型
export interface SysDept {
    deptId?: number
    parentId: number
    parentIds?: string
    deptName: string
    orderNum?: number
    leaderId?: number
    status: number // 0正常 1停用
    delFlag?: number
    createTime?: string
    updateTime?: string
}

// 1. 获取部门全量列表: GET /dept/list
export function getDeptList(): Promise<SysDept[]> {
    return request({
        url: '/dept/list',
        method: 'get',
    })
}

// 2. 根据 ID 查询详情: GET /dept/{deptId}
export function getDeptById(deptId: number): Promise<SysDept> {
    return request({
        url: `/dept/${deptId}`,
        method: 'get',
    })
}

// 3. 新增部门: POST /dept
export function addDept(data: SysDept): Promise<void> {
    return request({
        url: '/dept',
        method: 'post',
        data,
    })
}

// 4. 修改部门: PUT /dept
export function updateDept(data: SysDept): Promise<void> {
    return request({
        url: '/dept',
        method: 'put',
        data,
    })
}

// 5. 删除部门: DELETE /dept/{deptId}
export function deleteDept(deptId: number): Promise<void> {
    return request({
        url: `/dept/${deptId}`,
        method: 'delete',
    })
}