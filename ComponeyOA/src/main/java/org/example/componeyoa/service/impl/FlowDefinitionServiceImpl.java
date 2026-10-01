package org.example.componeyoa.service.impl;

import org.example.componeyoa.dao.FlowDefinitionMapper;
import org.example.componeyoa.entity.FlowDefinition;
import org.example.componeyoa.service.FlowDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 流程定义模板业务逻辑实现类
 */
@Service
public class FlowDefinitionServiceImpl implements FlowDefinitionService {

    @Autowired
    private FlowDefinitionMapper flowDefinitionMapper;

    @Override
    public boolean addFlowDefinition(FlowDefinition flowDefinition) {
        if (flowDefinition == null || flowDefinition.getFlowCode() == null) {
            return false;
        }

        // 1. 唯一性校验：检查 flow_code 是否已存在
        FlowDefinition existFlow = flowDefinitionMapper.selectFlowDefinitionByCode(flowDefinition.getFlowCode().trim());
        if (existFlow != null) {
            // 业务编码已存在，不允许重复新增
            return false;
        }

        // 2. 执行新增并基于受影响行数判定
        int rows = flowDefinitionMapper.insertFlowDefinition(flowDefinition);
        return rows > 0;
    }

    @Override
    public FlowDefinition getFlowDefinitionById(Long defId) {
        if (defId == null) {
            return null;
        }
        return flowDefinitionMapper.selectFlowDefinitionById(defId);
    }

    @Override
    public List<FlowDefinition> listFlowDefinitions(FlowDefinition flowDefinition) {
        return flowDefinitionMapper.selectFlowDefinitionList(flowDefinition);
    }

    @Override
    public boolean updateFlowDefinition(FlowDefinition flowDefinition) {
        if (flowDefinition == null || flowDefinition.getDefId() == null) {
            return false;
        }

        // 1. 校验当前待修改的数据是否存在
        FlowDefinition oldFlow = flowDefinitionMapper.selectFlowDefinitionById(flowDefinition.getDefId());
        if (oldFlow == null) {
            return false;
        }

        // 2. 如果修改了 flow_code，需排查是否与其它记录冲突
        if (flowDefinition.getFlowCode() != null && !flowDefinition.getFlowCode().trim().isEmpty()) {
            FlowDefinition checkCode = flowDefinitionMapper.selectFlowDefinitionByCode(flowDefinition.getFlowCode().trim());
            // 如果查出来的记录存在，且 ID 不是当前正在修改的 ID，则说明冲突
            if (checkCode != null && !checkCode.getDefId().equals(flowDefinition.getDefId())) {
                return false;
            }
        }

        // 3. 执行动态修改
        int rows = flowDefinitionMapper.updateFlowDefinition(flowDefinition);
        return rows > 0;
    }

    @Override
    public boolean deleteFlowDefinitionById(Long defId) {
        if (defId == null) {
            return false;
        }

        // 1. 确认该记录是否存在
        FlowDefinition oldFlow = flowDefinitionMapper.selectFlowDefinitionById(defId);
        if (oldFlow == null) {
            return false;
        }

        // 2. 后续扩展点：检查是否有关联的子节点 (flow_node_config) 或流转中的实例 (flow_instance)
        // 当前单表阶段直接执行逻辑删除
        int rows = flowDefinitionMapper.deleteFlowDefinitionById(defId);
        return rows > 0;
    }
}