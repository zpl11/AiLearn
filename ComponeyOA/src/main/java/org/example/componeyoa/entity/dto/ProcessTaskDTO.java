package org.example.componeyoa.entity.dto;

/**
 * 审批处理入参传输对象 (DTO)
 * 用于 Controller 层接收 POST/PUT 请求体
 */
public class ProcessTaskDTO {

    private Long taskId;
    private Integer status; // 1同意 2驳回[cite: 2]
    private String comment; // 审批意见

    public ProcessTaskDTO() {
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
