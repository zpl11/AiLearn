package org.example.componeyoa.service;

import org.example.componeyoa.common.PageResult;
import org.example.componeyoa.entity.SysOperLog;
import org.example.componeyoa.entity.dto.SysOperLogQueryDTO;

import java.util.List;

public interface SysOperLogService {

    /**
     * 新增操作日志
     */
    boolean insertOperLog(SysOperLog operLog);

    /**
     * 多条件分页查询操作日志
     */
    PageResult<SysOperLog> queryOperLogPage(SysOperLogQueryDTO queryDTO);

    /**
     * 获取单条日志详情
     */
    SysOperLog queryOperLogById(Long operId);

    /**
     * 批量或单条删除日志
     */
    boolean deleteOperLogByIds(List<Long> operIds);

    /**
     * 清空全部操作日志
     */
    void cleanOperLog();
}
