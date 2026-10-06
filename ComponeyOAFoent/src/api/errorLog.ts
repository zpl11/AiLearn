import request from '@/utils/request'

export interface SysErrorLog {
  errorId: number
  requestUri: string
  requestMethod: string
  errorName: string
  errorMessage: string
  errorStack: string
  operName: string
  createTime: string
}

export interface ErrorLogQuery {
  errorName?: string
  requestUri?: string
  operName?: string
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

// 1. 分页查询异常监控日志列表
export const getErrorLogList = (params: ErrorLogQuery) => {
  return request.get<any, PageResult<SysErrorLog>>('/system/errorlog/list', {
    params
  })
}

// 2. 根据 ID 查询单条异常日志详情（含完整调用栈）
export const getErrorLogDetail = (errorId: number) => {
  return request.get<any, SysErrorLog>(`/system/errorlog/${errorId}`)
}

// 3. 批量或单条删除异常日志
export const deleteErrorLog = (errorIds: string | number) => {
  return request.delete<any, void>('/system/errorlog/delete', {
    params: { errorIds }
  })
}

// 4. 清空全部异常日志
export const cleanErrorLog = () => {
  return request.delete<any, void>('/system/errorlog/clean')
}
