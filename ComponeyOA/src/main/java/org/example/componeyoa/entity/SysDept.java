package org.example.componeyoa.entity;

import java.time.LocalDateTime;

/**
 * 在 entity 中需要使用装饰类型来对属性进行声明
 * Boolean Long BigInt LocalDateTime 这种方式
 * 基本类型有  数值类型:byte shot int long  浮点数:float double 布尔类型:boolean  字符类型:char
 * */
public class SysDept {
    //在这里写上对应的 entity 实体类。
    private Long deptId; // 使用装饰字符 + 驼峰命名法来命名 因为在 Class 中使用装饰类命名的东西能够为 Null
    private Long parentId; // 一般在 SQL 中使用 bigInt 声明的属性都是用 Long 引用类型来进行处理
    private String parentIds;
    private String deptName;
    private Long leaderId;
    private Integer status;// 而一般 TINY 类型的 SQL 一般使用 Integer 的方式进行处理。
    private Integer delFlag;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer orderNum;

    public SysDept(){

    }

    public Long getDeptId() {
        return deptId;
    }

    public Long getParentId() {
        return parentId;
    }

    public String getParentIds() {
        return parentIds;
    }

    public String getDeptName() {
        return deptName;
    }

    public Long getLeaderId() {
        return leaderId;
    }

    public Integer getStatus() {
        return status;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public void setParentIds(String parentIds) {
        this.parentIds = parentIds;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public void setLeaderId(Long leaderId) {
        this.leaderId = leaderId;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    @Override
    public String toString() {
        return "SysDept{" +
                "deptId=" + deptId +
                ", parentId=" + parentId +
                ", parentIds='" + parentIds + '\'' +
                ", deptName='" + deptName + '\'' +
                ", leaderId=" + leaderId +
                ", status=" + status +
                ", delFlag=" + delFlag +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
