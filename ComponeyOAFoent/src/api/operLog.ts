import request from '@/utils/request'

export interface SysOperLog {
  operId: number
  title: string
  businessType: number
  method: string
  requestMethod: string
  operName: string
  deptName?: string
  operUrl: string
  operIp: string
  operParam?: string
  jsonResult?: string
  status: number
  errorMsg?: string
  costTime: number
  operTime: string
}

export interface OperLogQuery {
  title?: string
  operName?: string
  businessType?: number | string
  status?: number | string
  beginTime?: string
  endTime?: string
  pageNum: number
  pageSize: number
}

export interface PageResult<T> {
  total: number
  list: T[]
  pageNum: number
  pageSize: number
}

// 1. 分页查询操作日志列表
export const getOperLogList = (params: OperLogQuery) => {
  return request.get<any, PageResult<SysOperLog>>('/system/operlog/list', {
    params
  })
}

// 2. 根据 ID 查询单条操作日志详情
export const getOperLogDetail = (operId: number) => {
  return request.get<any, SysOperLog>(`/system/operlog/${operId}`)
}

// 3. 批量或单条删除操作日志
export const deleteOperLog = (operIds: string | number) => {
  return request.delete<any, void>('/system/operlog/delete', {
    params: { operIds }
  })
}

// 4. 清空全部操作日志
export const cleanOperLog = () => {
  return request.delete<any, void>('/system/operlog/clean')
}
