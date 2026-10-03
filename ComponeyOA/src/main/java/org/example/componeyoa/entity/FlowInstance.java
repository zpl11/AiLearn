package org.example.componeyoa.entity;

import java.time.LocalDateTime;

/**
 * 审批流程实例主表 flow_instance
 * 对应数据库：flow_instance
 */
public class FlowInstance {

    /**
     * 流程实例ID(事务主键)
     */
    private Long instanceId;

    /**
     * 关联的流程定义ID flow_definition.def_id
     */
    private Long defId;

    /**
     * 流程编码，冗余字段 LEAVE_APPLY / EXPENSE
     */
    private String flowCode;

    /**
     * 审批标题
     */
    private String title;

    /**
     * 发起人用户ID
     */
    private Long initiatorId;

    /**
     * 发起人部门ID
     */
    private Long deptId;

    /**
     * 业务表单快照 JSON字符串，TEXT类型对应String
     */
    private String formData;

    /**
     * 当前流转节点序号，对应flow_node_config.node_order
     */
    private Integer currentOrder;

    /**
     * 流程整体状态
     * 0处理中 1已通过 2已驳回 3已撤销
     */
    private Integer status;

    /**
     * 删除标志：0正常 1删除
     */
    private Integer delFlag;

    /**
     * 发起时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    //【强制要求：无参公开构造，MyBatis反射newInstance()创建对象】
    public FlowInstance() {
    }

    // 全参构造（业务可选使用）
    public FlowInstance(Long instanceId, Long defId, String flowCode, String title, Long initiatorId, Long deptId,
                        String formData, Integer currentOrder, Integer status, Integer delFlag,
                        LocalDateTime createTime, LocalDateTime updateTime) {
        this.instanceId = instanceId;
        this.defId = defId;
        this.flowCode = flowCode;
        this.title = title;
        this.initiatorId = initiatorId;
        this.deptId = deptId;
        this.formData = formData;
        this.currentOrder = currentOrder;
        this.status = status;
        this.delFlag = delFlag;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    // getter & setter
    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public Long getDefId() {
        return defId;
    }

    public void setDefId(Long defId) {
        this.defId = defId;
    }

    public String getFlowCode() {
        return flowCode;
    }

    public void setFlowCode(String flowCode) {
        this.flowCode = flowCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getInitiatorId() {
        return initiatorId;
    }

    public void setInitiatorId(Long initiatorId) {
        this.initiatorId = initiatorId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getFormData() {
        return formData;
    }

    public void setFormData(String formData) {
        this.formData = formData;
    }

    public Integer getCurrentOrder() {
        return currentOrder;
    }

    public void setCurrentOrder(Integer currentOrder) {
        this.currentOrder = currentOrder;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "FlowInstance{" +
                "instanceId=" + instanceId +
                ", defId=" + defId +
                ", flowCode='" + flowCode + '\'' +
                ", title='" + title + '\'' +
                ", initiatorId=" + initiatorId +
                ", deptId=" + deptId +
                ", formData='" + formData + '\'' +
                ", currentOrder=" + currentOrder +
                ", status=" + status +
                ", delFlag=" + delFlag +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}