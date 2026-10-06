package org.example.componeyoa.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统异常与任务监控日志实体对象 sys_error_log
 */
public class SysErrorLog implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 错误日志主键 */
    private Long errorId;

    /** 请求URL */
    private String requestUri = "";

    /** 请求方式 (GET, POST等) */
    private String requestMethod = "";

    /** 异常名称/类名 */
    private String errorName = "";

    /** 异常详细信息摘要 */
    private String errorMessage = "";

    /** 完整的错误堆栈信息 */
    private String errorStack = "";

    /** 操作人账号(若已登录) */
    private String operName = "";

    /** 发生时间 */
    private LocalDateTime createTime;

    public SysErrorLog() {}

    public Long getErrorId() {
        return errorId;
    }

    public void setErrorId(Long errorId) {
        this.errorId = errorId;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri != null ? requestUri : "";
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod != null ? requestMethod : "";
    }

    public String getErrorName() {
        return errorName;
    }

    public void setErrorName(String errorName) {
        this.errorName = errorName != null ? errorName : "";
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage != null ? errorMessage : "";
    }

    public String getErrorStack() {
        return errorStack;
    }

    public void setErrorStack(String errorStack) {
        this.errorStack = errorStack != null ? errorStack : "";
    }

    public String getOperName() {
        return operName;
    }

    public void setOperName(String operName) {
        this.operName = operName != null ? operName : "";
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "SysErrorLog{" +
                "errorId=" + errorId +
                ", requestUri='" + requestUri + '\'' +
                ", requestMethod='" + requestMethod + '\'' +
                ", errorName='" + errorName + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", operName='" + operName + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
