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
