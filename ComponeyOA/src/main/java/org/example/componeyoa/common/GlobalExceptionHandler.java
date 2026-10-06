package org.example.componeyoa.common;

import org.example.componeyoa.service.SysErrorLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器：兜底捕获所有 Controller 抛出的异常，自动写入 sys_error_log 监控库
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Autowired
    private SysErrorLogService errorLogService;

    /**
     * 捕获业务及通用运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        log.error("运行时异常 [{}]: {}", request.getRequestURI(), e.getMessage(), e);
        recordError(e, request);
        return Result.error(e.getMessage() != null && !e.getMessage().isEmpty() ? e.getMessage() : "系统运行时发生异常");
    }

    /**
     * 捕获全量兜底 Exception 异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("系统未知异常 [{}]: {}", request.getRequestURI(), e.getMessage(), e);
        recordError(e, request);
        return Result.error("系统繁忙，请联系管理员");
    }

    private void recordError(Throwable e, HttpServletRequest request) {
        try {
            String uri = request != null ? request.getRequestURI() : "";
            String method = request != null ? request.getMethod() : "";
            String operName = UserContext.getUserName();
            errorLogService.recordException(e, uri, method, operName);
        } catch (Exception ex) {
            log.error("GlobalExceptionHandler 自动记录监控日志失败: {}", ex.getMessage());
        }
    }
}
