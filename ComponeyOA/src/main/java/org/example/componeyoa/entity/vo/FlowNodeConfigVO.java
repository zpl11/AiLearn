package org.example.componeyoa.entity.vo;

import org.example.componeyoa.entity.FlowNodeConfig;

/**
 * 流程节点配置视图对象 (用于前端展示)
 */
public class FlowNodeConfigVO extends FlowNodeConfig {

    /**
     * 审批岗位名称 (关联 sys_post 表冗余展示字段)
     */
    private String postName;

    public FlowNodeConfigVO() {
        super();
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    @Override
    public String toString() {
        return "FlowNodeConfigVO{" +
                "postName='" + postName + '\'' +
                "} " + super.toString();
    }
}
