package org.example.componeyoa.common.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.componeyoa.common.UserContext;
import org.example.componeyoa.common.annotation.Log;
import org.example.componeyoa.entity.SysOperLog;
import org.example.componeyoa.service.SysOperLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 操作日志切面：自动捕获标注了 @Log 注解的方法调用并异步/安全入库
 */
@Aspect
@Component
public class LogAspect {

    private static final Logger log = LoggerFactory.getLogger(LogAspect.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private SysOperLogService operLogService;

    @Around("@annotation(controllerLog)")
    public Object doAround(ProceedingJoinPoint joinPoint, Log controllerLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        SysOperLog operLog = new SysOperLog();
        Object result = null;
        Throwable exception = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            exception = e;
            throw e;
        } finally {
            long costTime = System.currentTimeMillis() - startTime;
            try {
                handleLog(joinPoint, controllerLog, operLog, result, exception, costTime);
            } catch (Exception ex) {
                log.error("操作日志切面记录失败: {}", ex.getMessage(), ex);
            }
        }
    }

    private void handleLog(ProceedingJoinPoint joinPoint, Log controllerLog, SysOperLog operLog,
                           Object result, Throwable exception, long costTime) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            operLog.setOperUrl(request.getRequestURI());
            operLog.setRequestMethod(request.getMethod());
            operLog.setOperIp(getIpAddress(request));
        }

        // 获取当前登录用户
        String currentUserName = UserContext.getUserName();
        operLog.setOperName(currentUserName != null && !currentUserName.isEmpty() ? currentUserName : "系统用户");

        // 注解参数
        operLog.setTitle(controllerLog.title());
        operLog.setBusinessType(controllerLog.businessType());

        // 类名与方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        operLog.setMethod(className + "." + methodName + "()");

        // 消耗时间
        operLog.setCostTime(costTime);

        // 状态与异常信息
        if (exception != null) {
            operLog.setStatus(1); // 1异常
            operLog.setErrorMsg(truncateString(exception.getMessage() != null ? exception.getMessage() : exception.toString(), 2000));
        } else {
            operLog.setStatus(0); // 0正常
        }

        // 请求参数
        if (controllerLog.isSaveRequestData()) {
            try {
                Object[] args = joinPoint.getArgs();
                List<Object> validArgs = new ArrayList<>();
                if (args != null) {
                    for (Object arg : args) {
                        if (arg instanceof HttpServletRequest || arg instanceof HttpServletResponse) {
                            continue;
                        }
                        validArgs.add(arg);
                    }
                }
                String params = objectMapper.writeValueAsString(validArgs);
                operLog.setOperParam(truncateString(params, 2000));
            } catch (Exception e) {
                operLog.setOperParam("参数序列化异常: " + e.getMessage());
            }
        }

        // 返回结果
        if (controllerLog.isSaveResponseData() && result != null) {
            try {
                String jsonResult = objectMapper.writeValueAsString(result);
                operLog.setJsonResult(truncateString(jsonResult, 2000));
            } catch (Exception e) {
                operLog.setJsonResult("返回结果序列化异常: " + e.getMessage());
            }
        }

        // 入库
        boolean success = operLogService.insertOperLog(operLog);
        if (success) {
            log.info("【操作审计日志】记录成功: 模块=[{}], 类型=[{}], 操作人=[{}], URL=[{}], 耗时=[{}ms]",
                    operLog.getTitle(), operLog.getBusinessType(), operLog.getOperName(), operLog.getOperUrl(), operLog.getCostTime());
        } else {
            log.warn("【操作审计日志】入库失败: 模块=[{}], 操作人=[{}]", operLog.getTitle(), operLog.getOperName());
        }
    }

    private static String truncateString(String str, int maxLen) {
        if (str == null) return "";
        return str.length() > maxLen ? str.substring(0, maxLen) + "..." : str;
    }

    /**
     * 获取真实客户端 IP 地址
     */
    private static String getIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }
}
