package org.example.componeyoa.service.impl;

import org.example.componeyoa.dao.FlowInstanceMapper;
import org.example.componeyoa.entity.FlowInstance;
import org.example.componeyoa.service.FlowInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FlowInstanceServiceImpl implements FlowInstanceService {

    @Autowired
    private FlowInstanceMapper flowInstanceMapper;

    /**
     * 发起审批新增流程实例
     * 注意：真实业务，本方法内部还要生成 flow_task 审批待办任务
     * 【事务】后续你把生成task的逻辑写进来，要保证实例与任务同时入库，不能一部分成功一部分失败
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addFlowInstance(FlowInstance flowInstance) {
        // mapper返回受影响行数
        int rows = flowInstanceMapper.insert(flowInstance);
        // 插入完成后 flowInstance 对象已经回填 instanceId（mapper @Options生效）
        Long newInstanceId = flowInstance.getInstanceId();
        if(rows > 0){
            // ==========================
            // TODO 这里写业务：根据flow_node_config节点配置批量生成 flow_task待办任务
            // 使用 newInstanceId 作为外键填入 flow_task.instance_id
            // ==========================
            return true;
        }
        return false;
    }

    @Override
    public FlowInstance getById(Long instanceId) {
        return flowInstanceMapper.selectById(instanceId);
    }

    @Override
    public boolean updateFlowInstance(FlowInstance flowInstance) {
        int rows = flowInstanceMapper.update(flowInstance);
        return rows > 0;
    }

    @Override
    public boolean removeLogicById(Long instanceId) {
        int rows = flowInstanceMapper.deleteLogicById(instanceId);
        return rows > 0;
    }

    @Override
    public List<FlowInstance> getMyInitiateList(Long initiatorId) {
        // delFlag固定0：只查询未逻辑删除的数据
        return flowInstanceMapper.selectMyInitiateList(initiatorId,0);
    }

    @Override
    public List<FlowInstance> getList(String flowCode, Integer status, Long initiatorId) {
        // delFlag固定0
        return flowInstanceMapper.selectList(flowCode,status,initiatorId,0);
    }

}
