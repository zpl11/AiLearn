import request from '@/utils/request'

export interface FlowNodeConfig {
    nodeId?: number
    defId: number
    nodeName: string
    nodeOrder: number
    postId: number | null
    approveType: number // 0或签 1会签
    postName?: string
    createTime?: string
    updateTime?: string
}

export interface PostOption {
    postId: number
    postName: string
    postCode: string
}

// 1. 获取指定流程定义的节点列表 (按 nodeOrder 升序且带 postName)
export const getFlowNodeList = (defId: number) => {
    return request.get<any, FlowNodeConfig[]>(`/flow/node/list/${defId}`)
}

// 2. 新增审批节点
export const addFlowNode = (data: FlowNodeConfig) => {
    return request.post<any, void>('/flow/node', data)
}

// 3. 修改审批节点
export const updateFlowNode = (data: FlowNodeConfig) => {
    return request.put<any, void>('/flow/node', data)
}

// 4. 删除审批节点
export const deleteFlowNode = (nodeId: number) => {
    return request.delete<any, void>(`/flow/node/${nodeId}`)
}

// 5. 获取所有岗位字典列表 (用于下拉绑定)
export const getAllPostList = () => {
    return request.get<any, PostOption[]>('/system/post/listAll')
}