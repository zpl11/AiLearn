package org.example.componeyoa.entity.dto;

import java.io.Serializable;

/**
 * 监控异常日志分页与条件查询 DTO
 */
public class SysErrorLogQueryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 异常名称/类名（模糊匹配） */
    private String errorName;

    /** 请求URL（模糊匹配） */
    private String requestUri;

    /** 操作人员账号（模糊匹配） */
    private String operName;

    /** 开始时间 (yyyy-MM-dd HH:mm:ss) */
    private String beginTime;

    /** 结束时间 (yyyy-MM-dd HH:mm:ss) */
    private String endTime;

    /** 页码，默认1 */
    private Integer pageNum = 1;

    /** 每页大小，默认10 */
    private Integer pageSize = 10;

    public SysErrorLogQueryDTO() {}

    /**
     * 计算 SQL LIMIT 的偏移量 offset
     */
    public int getOffset() {
        int page = (pageNum == null || pageNum < 1) ? 1 : pageNum;
        int size = (pageSize == null || pageSize < 1) ? 10 : pageSize;
        return (page - 1) * size;
    }

    public String getErrorName() {
        return errorName;
    }

    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getOperName() {
        return operName;
    }

    public void setOperName(String operName) {
        this.operName = operName;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getPageNum() {
        return pageNum != null ? pageNum : 1;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize != null ? pageSize : 10;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
