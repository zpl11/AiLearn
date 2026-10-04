package org.example.componeyoa.dao;

import org.apache.ibatis.annotations.*;
import org.example.componeyoa.entity.FlowTask;
import org.example.componeyoa.entity.vo.FlowTaskVO;

import java.util.List;

@Mapper
public interface FlowTaskMapper {

    // ==================== 1. 系统派发任务 (引擎级 Insert) ====================
    /**
     * 新增审批任务明细（由系统流转引擎在生成新节点时调用，前端不直接调用）
     * useGeneratedKeys 回填自增主键 taskId
     */
    @Insert("INSERT INTO flow_task (" +
            "instance_id, node_id, step_order, node_name, assignee_id, task_status, comment, finish_time, create_time" +
            ") VALUES (" +
            "#{instanceId}, #{nodeId}, #{stepOrder}, #{nodeName}, #{assigneeId}, #{taskStatus}, #{comment}, #{finishTime}, NOW()" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "taskId")
    int insertTask(FlowTask flowTask);


    // ==================== 2. 审批处理 (Update 状态机) ====================
    /**
     * 审批流转：修改任务状态（同意/驳回）、追加审批意见、记录完成时间
     * 使用 <script> 标签实现动态 SQL 更新，防止覆盖非目标字段
     */
    @Update("<script>" +
            "UPDATE flow_task " +
            "<set>" +
            "  <if test='taskStatus != null'> task_status = #{taskStatus}, </if>" +
            "  <if test='comment != null and comment != \"\"'> comment = #{comment}, </if>" +
            "  <if test='finishTime != null'> finish_time = #{finishTime}, </if>" +
            "</set>" +
            "WHERE task_id = #{taskId}" +
            "</script>")
    int updateTask(FlowTask flowTask);


    // ==================== 3. 基础查询 (单表) ====================
    /**
     * 根据任务ID查询单条任务详情
     */
    @Select("SELECT task_id, instance_id, node_id, step_order, node_name, " +
            "assignee_id, task_status, comment, finish_time, create_time " +
            "FROM flow_task WHERE task_id = #{taskId}")
    FlowTask selectTaskById(@Param("taskId") Long taskId);

    /**
     * 基础单表查询：根据执行人和任务状态查询任务（支撑【我的待办/已办】基础数据）
     * taskStatus: 0待处理 1已同意 2已驳回 3已关闭/抢签作废
     * 完美利用数据库设计的 idx_assignee_status 联合索引[cite: 2]
     */
    @Select("<script>" +
            "SELECT task_id, instance_id, node_id, step_order, node_name, " +
            "assignee_id, task_status, comment, finish_time, create_time " +
            "FROM flow_task " +
            "WHERE assignee_id = #{assigneeId} " +
            "<if test='taskStatus != null'>" +
            "  AND task_status = #{taskStatus} " +
            "</if>" +
            "ORDER BY create_time DESC" +
            "</script>")
    List<FlowTask> selectTasksByAssigneeAndStatus(@Param("assigneeId") Long assigneeId, @Param("taskStatus") Integer taskStatus);


    // ==================== 4. 进阶综合查询 (联表 VO，核心业务场景) ====================
    /**
     * 联表查询：根据执行人和任务状态，关联实例主表查询完整的审批面板数据
     * 支撑前端展示“是谁(initiator_id)、因为什么(title)、发起了什么审批”[cite: 2]
     * 返回值使用 VO (View Object) 接收联表产生的新字段[cite: 1]
     */
    @Select("<script>" +
            "SELECT t.task_id, t.instance_id, t.node_id, t.step_order, t.node_name, " +
            "       t.assignee_id, t.task_status, t.comment, t.finish_time, t.create_time, " +
            "       i.title, i.initiator_id, i.flow_code, i.form_data " +
            "FROM flow_task t " +
            "LEFT JOIN flow_instance i ON t.instance_id = i.instance_id " +
            "WHERE t.assignee_id = #{assigneeId} " +
            "<if test='taskStatus != null'>" +
            "  AND t.task_status = #{taskStatus} " +
            "</if>" +
            "ORDER BY t.create_time DESC" +
            "</script>")
    List<FlowTaskVO> selectTaskDetailsByAssignee(@Param("assigneeId") Long assigneeId, @Param("taskStatus") Integer taskStatus);

    // ==================== 5. 特殊场景操作 ====================
    /**
     * 作废同节点下的其他代办任务（适用于或签/抢签模式）
     * 场景：一个节点派发给多个人，其中一人完成审批后，其余人的待办任务自动置为 "3已关闭/作废"[cite: 2]
     */
    @Update("UPDATE flow_task SET task_status = 3, finish_time = NOW(), comment = '系统自动作废：该节点已被其他人处理' " +
            "WHERE instance_id = #{instanceId} " +
            "  AND step_order = #{stepOrder} " +
            "  AND task_status = 0")
    int cancelOtherTasksInSameStep(@Param("instanceId") Long instanceId, @Param("stepOrder") Integer stepOrder);

    /**
     * 统计某流程实例指定节点下，处于指定状态的任务数量（用于会签模式判定）
     */
    @Select("SELECT COUNT(1) FROM flow_task WHERE instance_id = #{instanceId} AND step_order = #{stepOrder} AND task_status = #{taskStatus}")
    int countTasksByStatus(@Param("instanceId") Long instanceId, @Param("stepOrder") Integer stepOrder, @Param("taskStatus") Integer taskStatus);

}
