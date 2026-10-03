import request from '@/utils/request'

export interface FlowInstanceVO{
    instanceId?: number
    defId?: number
    flowCode?: string
    flowName?: string
    title?: string
    initiatorId?: number
    initiatorNickName?: string
    deptId?: number
    formData?: string
    currentOrder?: number
    status?: number
    delFlag?: number
    createTime?: string
    updateTime?: string
}

// 新增的 DTO：发起申请传参[cite: 1]
export interface StartInstanceDTO {
    defId: number
    title: string
    formData: string // 序列化后的 JSON 字符串
}

/** 获取全部实例列表 */
export function getFlowInstanceList(){
    return request.get<FlowInstanceVO[]>('/flow/instance/list')
}

/** 获取我发起的 */
export function getFlowInstanceMyInitiate(){
    return request.get<FlowInstanceVO[]>('/flow/instance/myInitiate')
}

/** 获取单条详情 */
export function getFlowInstanceInfo(instanceId:number){
    return request.get<FlowInstanceVO>(`/flow/instance/${instanceId}`)
}

/** 逻辑删除 */
export function deleteFlowInstance(instanceId:number){
    return request.delete(`/flow/instance/${instanceId}`)
}

/**
 * 获取可用的流程定义列表 (用于发起申请时的下拉选择)
 * 拦截器已脱壳，直接返回数组类型
 */
export function getAvailableFlowDefs() {
    return request<{ defId: number; flowName: string; flowCode: string }[]>({
        url: '/flow/definition/available',
        method: 'get'
    })
}

/**
 * 发起审批实例
 * 拦截器已脱壳，无返回值直接传 void 或 any
 */
export function startFlowInstance(data: StartInstanceDTO) {
    return request<void>({
        url: '/flow/instance/start',
        method: 'post',
        data
    })
}
