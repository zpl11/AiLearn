import request from '@/utils/request'

export interface SysMenu {
    menuId?: number
    parentId?: number
    menuName: string
    orderNum: number
    path?: string
    component?: string
    menuType: 'M' | 'C' | 'F'
    perms?: string
    status: number
    createTime?: string
    children?: SysMenu[]
}

// 查询系统全部菜单列表（平铺）
export function getMenuList(params?: { menuName?: string }) {
    return request<SysMenu[]>({
        url: '/system/menu/list',
        method: 'get',
        params
    })
}

// 新增菜单
export function addMenu(data: SysMenu) {
    return request({
        url: '/system/menu',
        method: 'post',
        data
    })
}

// 修改菜单
export function updateMenu(data: SysMenu) {
    return request({
        url: '/system/menu',
        method: 'put',
        data
    })
}

// 删除菜单
export function deleteMenu(menuId: number) {
    return request({
        url: `/system/menu/${menuId}`,
        method: 'delete'
    })
}