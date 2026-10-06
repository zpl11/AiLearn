package org.example.componeyoa.service;

import org.example.componeyoa.common.PageResult;
import org.example.componeyoa.entity.SysErrorLog;
import org.example.componeyoa.entity.dto.SysErrorLogQueryDTO;

import java.util.List;

public interface SysErrorLogService {

    /**
     * 新增异常日志
     */
    boolean insertErrorLog(SysErrorLog errorLog);

    /**
     * 快捷记录异常日志（自动提取堆栈并存入数据库）
     */
    void recordException(Throwable throwable, String uri, String method, String operName);

    /**
     * 分页多条件查询异常日志
     */
    PageResult<SysErrorLog> queryErrorLogPage(SysErrorLogQueryDTO queryDTO);

    /**
     * 查询单条异常详情
     */
    SysErrorLog queryErrorLogById(Long errorId);

    /**
     * 批量或单条删除异常日志
     */
    boolean deleteErrorLogByIds(List<Long> errorIds);

    /**
     * 清空全部异常日志
     */
    void cleanErrorLog();
}
