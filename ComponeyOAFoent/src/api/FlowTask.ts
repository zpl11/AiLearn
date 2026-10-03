import request from '@/utils/request'

// 1. 对应后端的 FlowTaskVO[cite: 2]
export interface FlowTaskVO {
    taskId: string
    instanceId: string
    nodeId: string
    stepOrder: number
    nodeName: string
    assigneeId: string
    taskStatus: number // 0待处理 1已同意 2已驳回 3已关闭/抢签作废
    comment: string
    finishTime: string
    createTime: string
    // 实例关联字段
    title: string
    initiatorId: string
    flowCode: string
    formData: string // 业务表单数据JSON字符串
}

// 2. 对应后端的 ProcessTaskDTO
export interface ProcessTaskDTO {
    taskId: string
    status: number // 1同意 2驳回
    comment: string
}

// 3. 基础响应结构（假设对应你后端的 Result<T>）
export interface Result<T = any> {
    code: number
    msg: string
    data: T
}

/**
 * 获取我的待办任务列表 (GET /system/task/todo)[cite: 1]
 */
export function getMyTodoList() {
    return request<Result<FlowTaskVO[]>>({
        url: '/system/task/todo',
        method: 'get'
    })
}

/**
 * 获取我的已办任务列表 (GET /system/task/done)[cite: 1]
 */
export function getMyDoneList(status?: number) {
    return request<Result<FlowTaskVO[]>>({
        url: '/system/task/done',
        method: 'get',
        params: { status } // 对应 @RequestParam
    })
}

/**
 * 处理审批任务 (PUT /system/task/process)[cite: 1]
 */
export function processTask(data: ProcessTaskDTO) {
    return request<Result<void>>({
        url: '/system/task/process',
        method: 'put', // 修改操作使用 PUT[cite: 1]
        data // 对应 @RequestBody
    })
}