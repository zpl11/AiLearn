package org.example.componeyoa.service.impl;

import org.example.componeyoa.dao.FlowInstanceMapper;
import org.example.componeyoa.dao.FlowNodeConfigMapper; // 1. 引入查节点配置的 Mapper
import org.example.componeyoa.dao.SysUserPostMapper;     // 2. 引入查岗位用户的 Mapper
import org.example.componeyoa.dao.FlowTaskMapper;        // 3. 引入写待办任务的 Mapper
import org.example.componeyoa.entity.FlowInstance;
import org.example.componeyoa.entity.FlowNodeConfig;      // 对应节点实体
import org.example.componeyoa.entity.FlowTask;           // 对应待办任务实体
import org.example.componeyoa.service.FlowInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FlowInstanceServiceImpl implements FlowInstanceService {

    @Autowired
    private FlowInstanceMapper flowInstanceMapper;

    // ==========================================
    // ✅ 注入派发任务所需的三个核心 Mapper
    // (如果你的 Mapper 变量名或类名稍有不同，请根据你的项目实际情况微调)
    // ==========================================
    @Autowired
    private FlowNodeConfigMapper flowNodeConfigMapper;

    @Autowired
    private SysUserPostMapper sysUserPostMapper;

    @Autowired
    private FlowTaskMapper flowTaskMapper;

    /**
     * 发起审批新增流程实例，并自动派发第一步待办任务
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addFlowInstance(FlowInstance flowInstance) {
        // 1. 插入实例表，mapper 返回受影响行数
        int rows = flowInstanceMapper.insert(flowInstance);

        // 插入完成后 flowInstance 对象已经回填 instanceId（mapper @Options生效）
        Long newInstanceId = flowInstance.getInstanceId();
        Long defId = flowInstance.getDefId();

        if (rows > 0) {
            // ==========================================
            // ✅ 落地实现：根据 flow_node_config 自动生成第一步 flow_task 待办任务
            // ==========================================

            // 2.1 查询该流程“第 1 个节点”的配置（node_order = 1）
            // 提示：如果你的 FlowNodeConfigMapper 里方法名不同，请改成你实际的方法
            FlowNodeConfig firstNode = flowNodeConfigMapper.selectFirstNodeByDefId(defId);
            if (firstNode == null) {
                throw new RuntimeException("该流程模板未配置审批节点，无法发起！");
            }

            // 2.2 获取该节点绑定的审批岗位 ID (post_id)
            Long targetPostId = firstNode.getPostId();

            // 2.3 查询拥有该岗位的用户 ID 集合
            // 提示：如果你的 SysUserPostMapper 里方法名不同，请改成你实际的方法
            List<Long> userIds = sysUserPostMapper.selectUserIdsByPostId(targetPostId);
            if (userIds == null || userIds.isEmpty()) {
                throw new RuntimeException("当前审批岗位的绑定用户为空，无法分派待办任务！");
            }

            // 2.4 遍历岗位下的所有用户，批量/单条写入 flow_task 待办任务表
            for (Long userId : userIds) {
                FlowTask task = new FlowTask();
                task.setInstanceId(newInstanceId);
                task.setNodeId(firstNode.getNodeId());

                // ✅ 核心检查点：必须确保这里有给 stepOrder 赋值！
                task.setStepOrder(firstNode.getNodeOrder());

                task.setNodeName(firstNode.getNodeName());
                task.setAssigneeId(userId);
                task.setTaskStatus(0);

                flowTaskMapper.insertTask(task);
            }

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
        return flowInstanceMapper.selectMyInitiateList(initiatorId, 0);
    }

    @Override
    public List<FlowInstance> getList(String flowCode, Integer status, Long initiatorId) {
        return flowInstanceMapper.selectList(flowCode, status, initiatorId, 0);
    }
}