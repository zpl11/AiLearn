package org.example.componeyoa.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 流程节点配置实体表映射对象
 * 对应数据库表：flow_node_config
 */
public class FlowNodeConfig implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 节点配置ID(主键) */
    private Long nodeId;

    /** 所属流程定义ID(关联 flow_definition.def_id) */
    private Long defId;

    /** 节点名称(如: 部门主管审批、财务审核) */
    private String nodeName;

    /** 审批节点执行顺序(1 -> 2 -> 3 顺序推进) */
    private Integer nodeOrder;

    /** 审批岗位ID(关联 sys_post.post_id) */
    private Long postId;

    /** 审批模式(0或签/抢签: 岗位内任意一人同意即过; 1会签: 岗位内所有人都要同意) */
    private Integer approveType;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /**
     * 显式保留无参构造函数
     * 供 MyBatis 查询反射实例化对象，以及 Jackson 反序列化 JSON 时使用
     */
    public FlowNodeConfig() {
    }

    // ================= Getter & Setter 方法 =================

    public Long getNodeId() {
        return nodeId;
    }

    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    public Long getDefId() {
        return defId;
    }

    public void setDefId(Long defId) {
        this.defId = defId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public Integer getNodeOrder() {
        return nodeOrder;
    }

    public void setNodeOrder(Integer nodeOrder) {
        this.nodeOrder = nodeOrder;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    public Integer getApproveType() {
        return approveType;
    }

    public void setApproveType(Integer approveType) {
        this.approveType = approveType;
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
        return "FlowNodeConfig{" +
                "nodeId=" + nodeId +
                ", defId=" + defId +
                ", nodeName='" + nodeName + '\'' +
                ", nodeOrder=" + nodeOrder +
                ", postId=" + postId +
                ", approveType=" + approveType +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
