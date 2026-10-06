package org.example.componeyoa.entity.dto;

import java.io.Serializable;

/**
 * 操作日志分页与条件查询 DTO
 */
public class SysOperLogQueryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 模块标题（模糊查询） */
    private String title;

    /** 操作人员账号（模糊查询） */
    private String operName;

    /** 业务类型(0其它 1新增 2修改 3删除 4授权 5导出 6导入 7清空) */
    private Integer businessType;

    /** 操作状态(0正常 1异常) */
    private Integer status;

    /** 开始时间 (yyyy-MM-dd HH:mm:ss) */
    private String beginTime;

    /** 结束时间 (yyyy-MM-dd HH:mm:ss) */
    private String endTime;

    /** 页码，默认第1页 */
    private Integer pageNum = 1;

    /** 每页大小，默认10条 */
    private Integer pageSize = 10;

    public SysOperLogQueryDTO() {}

    /**
     * 计算 SQL LIMIT 的分页偏移量 offset
     */
    public int getOffset() {
        int page = (pageNum == null || pageNum < 1) ? 1 : pageNum;
        int size = (pageSize == null || pageSize < 1) ? 10 : pageSize;
        return (page - 1) * size;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOperName() {
        return operName;
    }

    public void setOperName(String operName) {
        this.operName = operName;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
