package org.example.componeyoa.service.impl;

import org.example.componeyoa.dao.FlowTaskMapper;
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

    // 推荐使用构造器注入，替代 @Autowired
    public FlowTaskServiceImpl(FlowTaskMapper flowTaskMapper) {
        this.flowTaskMapper = flowTaskMapper;
    }

    @Override
    public List<FlowTaskVO> getMyTodoList(Long userId) {
        // 传入状态 0，查询待处理任务，支撑【我的待办】业务[cite: 2]
        return flowTaskMapper.selectTaskDetailsByAssignee(userId, 0);
    }

    @Override
    public List<FlowTaskVO> getMyDoneList(Long userId, Integer status) {
        // 传入状态 1或2或3，支撑【我的已办】业务[cite: 2]
        return flowTaskMapper.selectTaskDetailsByAssignee(userId, status);
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 使用 @Transactional 事务注解的方式来保证事务安全执行[cite: 1]，确保并发与数据一致性[cite: 3]
    public boolean processTask(Long taskId, Integer status, String comment) {

        // 1. 边缘情况与防御性编程校验：如果参数为空，可以直接抛出自定义业务异常或 return false[cite: 1]
        if (taskId == null || status == null) {
            return false;
        }

        // 2. 利用你在 Entity 中保留的无参构造函数，new 出对象进行封装[cite: 1]
        FlowTask updateParam = new FlowTask();
        updateParam.setTaskId(taskId);
        updateParam.setTaskStatus(status);
        updateParam.setComment(comment);
        updateParam.setFinishTime(LocalDateTime.now());

        // 3. 执行修改操作
        int rows = flowTaskMapper.updateTask(updateParam);

        // 💡 第一性原理进阶：在真实的审批流引擎中，这里不能仅仅 return。
        // 如果当前任务同意(status=1)，需要在这里查询 flow_node_config 表，判断是否有下一个节点。
        // 如果有，则向 flow_task 插入一条新数据派发给下一个人；如果没有，则去更新 flow_instance 的总状态为已通过。
        // 正因为涉及这么多表的连环写入，类名上方的 @Transactional 才显得极其关键！

        // 4. 通过 mapper 受影响行数 > 0 的方式来判断 SQL 语句是否执行成功，决定 return true 还是 false[cite: 1]
        return rows > 0;
    }
}
