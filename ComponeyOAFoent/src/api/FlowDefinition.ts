import request from '@/utils/request'

// 定义流程定义实体在前端的 TypeScript 类型
export interface FlowDefinition {
    defId?: number
    flowCode: string
    flowName: string
    status: number // 0正常 1停用
    delFlag?: number
    remark?: string
    createBy?: string
    createTime?: string
    updateBy?: string
    updateTime?: string
}

// 1. 获取流程定义全量列表: GET /flow/definition/list
export function getFlowDefinitionList(params?: Partial<FlowDefinition>): Promise<FlowDefinition[]> {
    return request({
        url: '/flow/definition/list',
        method: 'get',
        params,
    })
}

// 2. 根据 ID 查询流程定义详情: GET /flow/definition/{defId}
export function getFlowDefinitionById(defId: number): Promise<FlowDefinition> {
    return request({
        url: `/flow/definition/${defId}`,
        method: 'get',
    })
}

// 3. 新增流程定义: POST /flow/definition
export function addFlowDefinition(data: FlowDefinition): Promise<void> {
    return request({
        url: '/flow/definition',
        method: 'post',
        data,
    })
}

// 4. 修改流程定义: PUT /flow/definition
export function updateFlowDefinition(data: FlowDefinition): Promise<void> {
    return request({
        url: '/flow/definition',
        method: 'put',
        data,
    })
}

// 5. 删除流程定义: DELETE /flow/definition/{defId}
export function deleteFlowDefinition(defId: number): Promise<void> {
    return request({
        url: `/flow/definition/${defId}`,
        method: 'delete',
    })
}