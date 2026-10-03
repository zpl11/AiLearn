package org.example.componeyoa.entity;

import java.time.LocalDateTime;

public class FlowTask {
    // 提醒：返回给前端时，为防止 JS 精度丢失，可以使用 Jackson 的 @JsonSerialize(using = ToStringSerializer.class) 将 Long 转为 String
    private Long taskId;
    private Long instanceId;
    private Long nodeId;
    private Integer stepOrder;
    private String nodeName;
    private Long assigneeId;
    private Integer taskStatus; // 0待处理 1已同意 2已驳回 3已关闭/抢签作废
    private String comment;
    private LocalDateTime finishTime;
    private LocalDateTime createTime;

    // 必须保留的无参构造函数，方便 MyBatis 反射创建对象并调用 Setter 赋值
    public FlowTask() {
    }

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
}
