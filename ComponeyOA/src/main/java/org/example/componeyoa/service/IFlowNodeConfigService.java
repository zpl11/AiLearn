package org.example.componeyoa.service;

import org.example.componeyoa.entity.FlowNodeConfig;
import org.example.componeyoa.entity.vo.FlowNodeConfigVO;

import java.util.List;

/**
 * 流程节点配置 业务接口
 */
public interface IFlowNodeConfigService {

    /**
     * 根据流程定义ID获取所有配置节点（按顺序排列并带有岗位名称）
     *
     * @param defId 流程定义ID
     * @return 节点视图列表
     */
    List<FlowNodeConfigVO> listByDefId(Long defId);

    /**
     * 根据主键查询节点详情
     *
     * @param nodeId 节点ID
     * @return 节点实体
     */
    FlowNodeConfig getById(Long nodeId);

    /**
     * 新增审批节点
     *
     * @param nodeConfig 节点配置信息
     * @return true-成功，false-失败
     */
    boolean addNode(FlowNodeConfig nodeConfig);

    /**
     * 修改审批节点
     *
     * @param nodeConfig 节点配置信息
     * @return true-成功，false-失败
     */
    boolean updateNode(FlowNodeConfig nodeConfig);

    /**
     * 删除审批节点
     *
     * @param nodeId 节点ID
     * @return true-成功，false-失败
     */
    boolean deleteNode(Long nodeId);
}
