import request from '@/utils/request'

export interface SysPost {
    postId?: number
    postCode: string
    postName: string
    postSort: number
    status: number
    createTime?: string
}

// 1. 获取全量岗位列表
export const getPostList = () => {
    return request.get<any, SysPost[]>('/system/post/queryAllPost')
}

// 2. 按名称模糊查询岗位
export const searchPostByName = (postName: string) => {
    return request.get<any, SysPost[]>('/system/post/queryPostByPostName', {
        params: { postName }
    })
}

// 3. 新增岗位
export const addPost = (data: SysPost) => {
    return request.post<any, void>('/system/post/addPost', data)
}

// 4. 修改岗位
export const updatePost = (data: SysPost) => {
    return request.put<any, void>('/system/post/updatePost', data)
}

// 5. 逻辑删除岗位
export const deletePost = (postId: number) => {
    return request.delete<any, void>('/system/post/delPost', {
        params: { postId }
    })
}