package org.example.componeyoa.service;

import org.example.componeyoa.entity.FlowInstance;

import java.util.List;

/**
 * 审批流程实例 Service接口
 */
public interface FlowInstanceService {

    /**
     * 发起审批，新增流程实例
     * @param flowInstance 流程实例实体
     * @return true成功 false失败
     */
    boolean addFlowInstance(FlowInstance flowInstance);

    /**
     * 根据主键查询实例详情
     * @param instanceId 实例id
     * @return FlowInstance
     */
    FlowInstance getById(Long instanceId);

    /**
     * 更新流程实例
     * @param flowInstance 实例实体
     * @return true成功 false失败
     */
    boolean updateFlowInstance(FlowInstance flowInstance);

    /**
     * 逻辑删除流程实例
     * @param instanceId 实例主键
     * @return true成功 false失败
     */
    boolean removeLogicById(Long instanceId);

    /**
     * 查询【我发起的】审批列表
     * @param initiatorId 发起人用户id
     * @return 实例集合（只查未删除 delFlag=0）
     */
    List<FlowInstance> getMyInitiateList(Long initiatorId);

    /**
     * 后台多条件查询流程实例列表
     * @param flowCode 流程编码
     * @param status 流程状态
     * @param initiatorId 发起人id
     * @return 实例集合（只查未删除 delFlag=0）
     */
    List<FlowInstance> getList(String flowCode, Integer status, Long initiatorId);

}
