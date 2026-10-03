package org.example.componeyoa.service.impl;

import org.example.componeyoa.dao.FlowNodeConfigMapper;
import org.example.componeyoa.entity.FlowNodeConfig;
import org.example.componeyoa.entity.vo.FlowNodeConfigVO;
import org.example.componeyoa.service.IFlowNodeConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 流程节点配置 业务实现类
 */
@Service
public class FlowNodeConfigServiceImpl implements IFlowNodeConfigService {

    @Autowired
    private FlowNodeConfigMapper nodeConfigMapper;

    @Override
    public List<FlowNodeConfigVO> listByDefId(Long defId) {
        if (defId == null || defId <= 0) {
            return Collections.emptyList();
        }
        return nodeConfigMapper.selectListByDefId(defId);
    }

    @Override
    public FlowNodeConfig getById(Long nodeId) {
        if (nodeId == null || nodeId <= 0) {
            return null;
        }
        return nodeConfigMapper.selectById(nodeId);
    }

    @Override
    public boolean addNode(FlowNodeConfig nodeConfig) {
        // 1. 基础业务入参校验
        validateNodeParams(nodeConfig);

        // 2. 业务排重：检查当前流程定义下，节点执行顺序是否重复
        int conflictCount = nodeConfigMapper.checkOrderUnique(
                nodeConfig.getDefId(),
                nodeConfig.getNodeOrder(),
                0L
        );
        if (conflictCount > 0) {
            throw new IllegalArgumentException("当前流程已存在序号为 [" + nodeConfig.getNodeOrder() + "] 的审批节点，请重新编排序号！");
        }

        // 3. 默认审批模式容错（若未传则默认为 0: 或签）
        if (nodeConfig.getApproveType() == null) {
            nodeConfig.setApproveType(0);
        }

        // 4. 执行插入并通过受影响行数判定
        int rows = nodeConfigMapper.insert(nodeConfig);
        return rows > 0;
    }

    @Override
    public boolean updateNode(FlowNodeConfig nodeConfig) {
        if (nodeConfig.getNodeId() == null || nodeConfig.getNodeId() <= 0) {
            throw new IllegalArgumentException("更新节点必须指定节点ID！");
        }

        // 1. 基础业务入参校验
        validateNodeParams(nodeConfig);

        // 2. 业务排重：排除自己后，检查是否与其他节点的序号发生冲突
        int conflictCount = nodeConfigMapper.checkOrderUnique(
                nodeConfig.getDefId(),
                nodeConfig.getNodeOrder(),
                nodeConfig.getNodeId()
        );
        if (conflictCount > 0) {
            throw new IllegalArgumentException("当前流程已存在序号为 [" + nodeConfig.getNodeOrder() + "] 的审批节点，请重新编排序号！");
        }

        // 3. 执行更新并通过受影响行数判定
        int rows = nodeConfigMapper.update(nodeConfig);
        return rows > 0;
    }

    @Override
    public boolean deleteNode(Long nodeId) {
        if (nodeId == null || nodeId <= 0) {
            return false;
        }
        int rows = nodeConfigMapper.deleteById(nodeId);
        return rows > 0;
    }

    /**
     * 私有辅助方法：公共业务字段有效性校验
     */
    private void validateNodeParams(FlowNodeConfig nodeConfig) {
        if (nodeConfig == null) {
            throw new IllegalArgumentException("节点配置参数不能为空！");
        }
        if (nodeConfig.getDefId() == null || nodeConfig.getDefId() <= 0) {
            throw new IllegalArgumentException("所属流程定义ID不能为空！");
        }
        if (nodeConfig.getNodeName() == null || nodeConfig.getNodeName().trim().isEmpty()) {
            throw new IllegalArgumentException("节点名称不能为空！");
        }
        if (nodeConfig.getNodeOrder() == null || nodeConfig.getNodeOrder() < 1) {
            throw new IllegalArgumentException("审批节点执行顺序必须是大于等于 1 的正整数！");
        }
        if (nodeConfig.getPostId() == null || nodeConfig.getPostId() <= 0) {
            throw new IllegalArgumentException("请选择该节点绑定的审批岗位！");
        }
    }
}
