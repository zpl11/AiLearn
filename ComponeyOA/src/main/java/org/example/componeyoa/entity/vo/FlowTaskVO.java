package org.example.componeyoa.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.time.LocalDateTime;

/**
 * 审批任务明细视图对象 (VO)
 * 支撑【我的待办】与【我的已办】前端列表展示，包含实例扩展信息
 */
public class FlowTaskVO {

    // ================== 来自 flow_task 表的字段 ==================

    @JsonSerialize(using = ToStringSerializer.class) // 防止前端JS超过16位精度丢失
    private Long taskId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long instanceId;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long nodeId;

    private Integer stepOrder;

    private String nodeName;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long assigneeId;

    private Integer taskStatus; // 0待处理 1已同意 2已驳回 3已关闭/抢签作废

    private String comment;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime finishTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;


    // ================== 来自 flow_instance 表的扩充字段 ==================

    private String title;           // 审批标题(如: 张三提交的请假申请)

    @JsonSerialize(using = ToStringSerializer.class)
    private Long initiatorId;       // 发起人用户ID

    private String flowCode;        // 流程编码(如: LEAVE_APPLY)

    private String formData;        // 业务表单内容快照(JSON字符串，前端可直接解析渲染只读表单)


    // ================== 构造函数 ==================

    /**
     * 必须保留的无参构造函数，支撑 MyBatis 和 Jackson 的 JVM 反射机制[cite: 1]
     */
    public FlowTaskVO() {
    }

    // ================== Getter 和 Setter ==================
    // (如果你项目中引入了 Lombok，可以直接在类头上加 @Data 注解，省略下面这堆代码)

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public Long getNodeId() {
        return nodeId;
    }

    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    public Integer getStepOrder() {
        return stepOrder;
    }

    public void setStepOrder(Integer stepOrder) {
        this.stepOrder = stepOrder;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
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

    public String getFlowCode() {
        return flowCode;
    }

    public void setFlowCode(String flowCode) {
        this.flowCode = flowCode;
    }

    public String getFormData() {
        return formData;
    }

    public void setFormData(String formData) {
        this.formData = formData;
    }
}