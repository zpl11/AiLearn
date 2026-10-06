package org.example.componeyoa.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作审计日志实体对象 sys_oper_log
 */
public class SysOperLog implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 日志主键 */
    private Long operId;

    /** 模块标题 */
    private String title = "";

    /** 业务类型(0其它 1新增 2修改 3删除 4授权 5导出 6导入 7清空) */
    private Integer businessType = 0;

    /** 方法名称 */
    private String method = "";

    /** 请求方式(GET, POST等) */
    private String requestMethod = "";

    /** 操作人员账号 */
    private String operName = "";

    /** 部门名称 */
    private String deptName = "";

    /** 请求URL */
    private String operUrl = "";

    /** 主机IP */
    private String operIp = "";

    /** 请求参数 */
    private String operParam = "";

    /** 返回参数 */
    private String jsonResult = "";

    /** 操作状态(0正常 1异常) */
    private Integer status = 0;

    /** 错误消息 */
    private String errorMsg = "";

    /** 消耗时间(毫秒) */
    private Long costTime = 0L;

    /** 操作时间 */
    private LocalDateTime operTime;

    public SysOperLog() {}

    public Long getOperId() {
        return operId;
    }

    public void setOperId(Long operId) {
        this.operId = operId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getOperName() {
        return operName;
    }

    public void setOperName(String operName) {
        this.operName = operName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getOperUrl() {
        return operUrl;
    }

    public void setOperUrl(String operUrl) {
        this.operUrl = operUrl;
    }

    public String getOperIp() {
        return operIp;
    }

    public void setOperIp(String operIp) {
        this.operIp = operIp;
    }

    public String getOperParam() {
        return operParam;
    }

    public void setOperParam(String operParam) {
        this.operParam = operParam;
    }

    public String getJsonResult() {
        return jsonResult;
    }

    public void setJsonResult(String jsonResult) {
        this.jsonResult = jsonResult;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Long getCostTime() {
        return costTime;
    }

    public void setCostTime(Long costTime) {
        this.costTime = costTime;
    }

    public LocalDateTime getOperTime() {
        return operTime;
    }

    public void setOperTime(LocalDateTime operTime) {
        this.operTime = operTime;
    }

    @Override
    public String toString() {
        return "SysOperLog{" +
                "operId=" + operId +
                ", title='" + title + '\'' +
                ", businessType=" + businessType +
                ", method='" + method + '\'' +
                ", requestMethod='" + requestMethod + '\'' +
                ", operName='" + operName + '\'' +
                ", deptName='" + deptName + '\'' +
                ", operUrl='" + operUrl + '\'' +
                ", operIp='" + operIp + '\'' +
                ", status=" + status +
                ", costTime=" + costTime +
                ", operTime=" + operTime +
                '}';
    }
}
