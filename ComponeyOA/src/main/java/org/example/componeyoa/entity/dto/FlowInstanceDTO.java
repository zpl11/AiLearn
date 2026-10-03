package org.example.componeyoa.entity.dto;

/**
 * 发起审批 请求DTO
 * 对应 flow_instance，前端提交申请表单入参
 */
public class FlowInstanceDTO {

    /**
     * 关联流程定义ID flow_definition.def_id
     */
    private Long defId;

    /**
     * 流程编码 如 LEAVE_APPLY、EXPENSE
     */
    private String flowCode;

    /**
     * 审批标题（前端传入：张三请假申请）
     */
    private String title;

    /**
     * 发起人ID：【注意！后端从登录上下文拿，前端不传，这个字段不放到DTO】
     */

    /**
     * 发起人部门ID：后端从登录用户上下文获取，前端不传
     */

    /**
     * 表单JSON快照，前端提交的业务表单数据
     */
    private String formData;

    // getter / setter
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

    public String getFormData() {
        return formData;
    }

    public void setFormData(String formData) {
        this.formData = formData;
    }
}
