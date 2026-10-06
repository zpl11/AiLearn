package org.example.componeyoa.service.impl;

import org.example.componeyoa.common.PageResult;
import org.example.componeyoa.dao.SysErrorLogMapper;
import org.example.componeyoa.entity.SysErrorLog;
import org.example.componeyoa.entity.dto.SysErrorLogQueryDTO;
import org.example.componeyoa.service.SysErrorLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

@Service
public class SysErrorLogServiceImpl implements SysErrorLogService {

    private static final Logger log = LoggerFactory.getLogger(SysErrorLogServiceImpl.class);

    @Autowired
    private SysErrorLogMapper errorLogMapper;

    @Override
    public boolean insertErrorLog(SysErrorLog errorLog) {
        if (errorLog == null) {
            return false;
        }
        return errorLogMapper.insertErrorLog(errorLog) > 0;
    }

    @Override
    public void recordException(Throwable throwable, String uri, String method, String operName) {
        if (throwable == null) {
            return;
        }
        try {
            SysErrorLog errorLog = new SysErrorLog();
            errorLog.setRequestUri(uri != null ? uri : "");
            errorLog.setRequestMethod(method != null ? method : "");
            errorLog.setErrorName(throwable.getClass().getName());
            errorLog.setErrorMessage(throwable.getMessage() != null ? truncate(throwable.getMessage(), 1000) : "无详细错误摘要");
            errorLog.setErrorStack(extractStackTrace(throwable, 8000));
            errorLog.setOperName(operName != null && !operName.isEmpty() ? operName : "系统用户");

            errorLogMapper.insertErrorLog(errorLog);
            log.info("【系统监控】已记录异常日志: URI=[{}], 异常=[{}], 操作人=[{}]", uri, errorLog.getErrorName(), operName);
        } catch (Exception e) {
            log.error("写入系统异常日志失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public PageResult<SysErrorLog> queryErrorLogPage(SysErrorLogQueryDTO queryDTO) {
        if (queryDTO == null) {
            queryDTO = new SysErrorLogQueryDTO();
        }
        long total = errorLogMapper.countErrorLogList(queryDTO);
        if (total == 0) {
            return PageResult.of(Collections.emptyList(), 0, queryDTO.getPageNum(), queryDTO.getPageSize());
        }
        List<SysErrorLog> list = errorLogMapper.selectErrorLogList(queryDTO);
        return PageResult.of(list, total, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public SysErrorLog queryErrorLogById(Long errorId) {
        if (errorId == null) {
            return null;
        }
        return errorLogMapper.selectErrorLogById(errorId);
    }

    @Override
    public boolean deleteErrorLogByIds(List<Long> errorIds) {
        if (errorIds == null || errorIds.isEmpty()) {
            return false;
        }
        return errorLogMapper.deleteErrorLogByIds(errorIds) > 0;
    }

    @Override
    public void cleanErrorLog() {
        errorLogMapper.cleanErrorLog();
    }

    private static String extractStackTrace(Throwable throwable, int maxLen) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        String stack = sw.toString();
        return truncate(stack, maxLen);
    }

    private static String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() > maxLen ? text.substring(0, maxLen) + "\n...(堆栈信息截断)" : text;
    }
}
