package org.example.componeyoa.entity.vo;

import java.time.LocalDateTime;

/**
 * 审批流程实例 返回VO
 * 用于【我发起的】列表、后台审批实例管理页面
 * 除数据库原有字段，扩展展示字段：发起人昵称nickName，流程定义名称flowName
 */
public class FlowInstanceVO {

    /**
     * 流程实例主键
     */
    private Long instanceId;

    /**
     * 流程定义ID
     */
    private Long defId;

    /**
     * 流程编码
     */
    private String flowCode;

    /**
     * 审批标题
     */
    private String title;

    /**
     * 发起人ID
     */
    private Long initiatorId;

    /**
     * 发起人部门ID
     */
    private Long deptId;

    /**
     * 表单快照JSON
     */
    private String formData;

    /**
     * 当前流转节点序号
     */
    private Integer currentOrder;

    /**
     * 流程状态：0处理中 1已通过 2已驳回 3已撤销
     */
    private Integer status;

    /**
     * 删除标记
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

    // ========= 扩展的页面展示字段（数据库flow_instance表不存在）==========
    /**
     * 发起人昵称（员工姓名）关联sys_user查询填充
     */
    private String initiatorNickName;

    /**
     * 流程模板名称，关联flow_definition查询填充
     */
    private String flowName;

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

    public String getInitiatorNickName() {
        return initiatorNickName;
    }

    public void setInitiatorNickName(String initiatorNickName) {
        this.initiatorNickName = initiatorNickName;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }
}
