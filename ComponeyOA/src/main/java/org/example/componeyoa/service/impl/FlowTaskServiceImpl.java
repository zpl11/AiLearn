package org.example.componeyoa.service.impl;

import org.example.componeyoa.dao.FlowInstanceMapper;
import org.example.componeyoa.dao.FlowNodeConfigMapper;
import org.example.componeyoa.dao.FlowTaskMapper;
import org.example.componeyoa.dao.SysUserPostMapper;
import org.example.componeyoa.entity.FlowInstance;
import org.example.componeyoa.entity.FlowNodeConfig;
import org.example.componeyoa.entity.FlowTask;
import org.example.componeyoa.entity.vo.FlowTaskVO;
import org.example.componeyoa.service.FlowTaskService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FlowTaskServiceImpl implements FlowTaskService {

    private final FlowTaskMapper flowTaskMapper;
    private final FlowInstanceMapper flowInstanceMapper;
    private final FlowNodeConfigMapper flowNodeConfigMapper;
    private final SysUserPostMapper sysUserPostMapper;

    public FlowTaskServiceImpl(FlowTaskMapper flowTaskMapper,
                               FlowInstanceMapper flowInstanceMapper,
                               FlowNodeConfigMapper flowNodeConfigMapper,
                               SysUserPostMapper sysUserPostMapper) {
        this.flowTaskMapper = flowTaskMapper;
        this.flowInstanceMapper = flowInstanceMapper;
        this.flowNodeConfigMapper = flowNodeConfigMapper;
        this.sysUserPostMapper = sysUserPostMapper;
    }

    @Override
    public List<FlowTaskVO> getMyTodoList(Long userId) {
        return flowTaskMapper.selectTaskDetailsByAssignee(userId, 0);
    }

    @Override
    public List<FlowTaskVO> getMyDoneList(Long userId, Integer status) {
        return flowTaskMapper.selectTaskDetailsByAssignee(userId, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean processTask(Long taskId, Integer status, String comment) {
        // 1. 防御性校验
        if (taskId == null || status == null) {
            return false;
        }

        // 2. 查询当前正在处理的任务明细
        FlowTask currentTask = flowTaskMapper.selectTaskById(taskId);
        if (currentTask == null) {
            throw new RuntimeException("审批任务不存在");
        }
        if (currentTask.getTaskStatus() != null && currentTask.getTaskStatus() != 0) {
            throw new RuntimeException("该任务已被处理，无需重复提交");
        }

        Long instanceId = currentTask.getInstanceId();
        Integer stepOrder = currentTask.getStepOrder();
        Long nodeId = currentTask.getNodeId();

        // 3. 执行当前任务状态更新（状态机变更：同意/驳回、批注、完成时间）
        FlowTask updateParam = new FlowTask();
        updateParam.setTaskId(taskId);
        updateParam.setTaskStatus(status);
        updateParam.setComment(comment != null ? comment.trim() : "");
        updateParam.setFinishTime(LocalDateTime.now());
        int rows = flowTaskMapper.updateTask(updateParam);
        if (rows <= 0) {
            return false;
        }

        // 4. 获取流程实例与当前节点配置信息
        FlowInstance instance = flowInstanceMapper.selectById(instanceId);
        if (instance == null) {
            throw new RuntimeException("对应的流程实例不存在");
        }
        FlowNodeConfig currentNode = flowNodeConfigMapper.selectById(nodeId);

        // 5. 核心工作流流转状态机驱动推进
        if (status == 2) {
            // ================= 场景 A：审批驳回 =================
            // 5.1 流程实例状态置为 2 (已驳回)
            flowInstanceMapper.updateStatus(instanceId, 2);

            // 5.2 将同节点下其他未办理的抢签待办作废关闭
            flowTaskMapper.cancelOtherTasksInSameStep(instanceId, stepOrder);

            return true;
        } else if (status == 1) {
            // ================= 场景 B：审批同意 =================
            // 5.3 检查是否为会签模式 (approve_type == 1)
            boolean canProceed = true;
            if (currentNode != null && currentNode.getApproveType() != null && currentNode.getApproveType() == 1) {
                int pendingCount = flowTaskMapper.countTasksByStatus(instanceId, stepOrder, 0);
                if (pendingCount > 0) {
                    canProceed = false; // 会签：同节点还有其他人员未审完，暂不推进
                }
            } else {
                // 或签/抢签模式 (approve_type == 0)：一人同意即通过，将其余人的待办自动作废
                flowTaskMapper.cancelOtherTasksInSameStep(instanceId, stepOrder);
            }

            if (canProceed) {
                // 5.4 查询下一个审批节点
                FlowNodeConfig nextNode = flowNodeConfigMapper.selectNextNode(instance.getDefId(), stepOrder);

                if (nextNode != null) {
                    // 存在下一节点：推进流程实例 current_order
                    flowInstanceMapper.updateProgress(instanceId, nextNode.getNodeOrder(), 0);

                    // 查询下一节点绑定的岗位用户
                    List<Long> nextUserIds = sysUserPostMapper.selectUserIdsByPostId(nextNode.getPostId());
                    if (nextUserIds == null || nextUserIds.isEmpty()) {
                        throw new RuntimeException("下一审批节点【" + nextNode.getNodeName() + "】暂无绑定员工，无法派发任务！");
                    }

                    // 批量生成下一节点的待办任务并派发给对应岗位员工
                    for (Long nextUserId : nextUserIds) {
                        FlowTask nextTask = new FlowTask();
                        nextTask.setInstanceId(instanceId);
                        nextTask.setNodeId(nextNode.getNodeId());
                        nextTask.setStepOrder(nextNode.getNodeOrder());
                        nextTask.setNodeName(nextNode.getNodeName());
                        nextTask.setAssigneeId(nextUserId);
                        nextTask.setTaskStatus(0); // 0: 待处理
                        flowTaskMapper.insertTask(nextTask);
                    }
                } else {
                    // 没有下一节点：已到达流程终点，实例状态置为 1 (已通过)
                    flowInstanceMapper.updateStatus(instanceId, 1);
                }
            }
            return true;
        }

        return false;
    }
}
