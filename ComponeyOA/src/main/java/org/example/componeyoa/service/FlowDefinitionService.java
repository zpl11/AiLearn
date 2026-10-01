package org.example.componeyoa.service;

import org.example.componeyoa.entity.FlowDefinition;

import java.util.List;

/**
 * 流程定义模板业务逻辑接口
 */
public interface FlowDefinitionService {

    /**
     * 新增流程定义
     *
     * @param flowDefinition 流程定义实体
     * @return true: 新增成功; false: 失败或编码已存在
     */
    boolean addFlowDefinition(FlowDefinition flowDefinition);

    /**
     * 根据主键查询流程定义详情
     *
     * @param defId 流程定义ID
     * @return 流程定义详情
     */
    FlowDefinition getFlowDefinitionById(Long defId);

    /**
     * 条件查询流程定义列表
     *
     * @param flowDefinition 查询条件过滤载体
     * @return 流程定义列表
     */
    List<FlowDefinition> listFlowDefinitions(FlowDefinition flowDefinition);

    /**
     * 修改流程定义
     *
     * @param flowDefinition 待更新实体
     * @return true: 更新成功; false: 失败或编码冲突
     */
    boolean updateFlowDefinition(FlowDefinition flowDefinition);

    /**
     * 删除流程定义 (逻辑删除)
     *
     * @param defId 流程定义ID
     * @return true: 删除成功; false: 删除失败
     */
    boolean deleteFlowDefinitionById(Long defId);
}