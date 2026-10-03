package org.example.componeyoa.dao;

import org.apache.ibatis.annotations.*;
import org.example.componeyoa.entity.FlowInstance;

import java.util.List;
/**
 * 审批流程实例 Mapper
 * 对应表 flow_instance
 */
@Mapper
public interface FlowInstanceMapper {

    // ==================== 1.新增流程实例 ====================
    /**
     * 新增审批流程实例
     * useGeneratedKeys 回填自增主键 instanceId
     */
    @Insert("INSERT INTO flow_instance (" +
            "def_id, flow_code, title, initiator_id, dept_id, form_data, current_order, status, del_flag, create_time, update_time" +
            ") VALUES (" +
            "#{defId}, #{flowCode}, #{title}, #{initiatorId}, #{deptId}, #{formData}, #{currentOrder}, #{status}, #{delFlag}, NOW(), NOW()" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "instanceId")
    int insert(FlowInstance flowInstance);

    // ==================== 2.根据主键查询 ====================
    /**
     * 根据 instanceId 查询单条流程实例详情
     */
    @Select("SELECT instance_id, def_id, flow_code, title, initiator_id, dept_id, form_data, current_order, status, del_flag, create_time, update_time " +
            "FROM flow_instance WHERE instance_id = #{instanceId}")
    FlowInstance selectById(@Param("instanceId") Long instanceId);

    // ==================== 3.更新流程实例 ====================
    /**
     * 全字段更新流程实例，update_time 使用数据库NOW()自动刷新
     */
    @Update("UPDATE flow_instance SET " +
            "def_id = #{defId}, " +
            "flow_code = #{flowCode}, " +
            "title = #{title}, " +
            "initiator_id = #{initiatorId}, " +
            "dept_id = #{deptId}, " +
            "form_data = #{formData}, " +
            "current_order = #{currentOrder}, " +
            "status = #{status}, " +
            "del_flag = #{delFlag}, " +
            "update_time = NOW() " +
            "WHERE instance_id = #{instanceId}")
    int update(FlowInstance flowInstance);

    // ====================4.逻辑删除 ====================
    /**
     * 逻辑删除：设置 del_flag = 1，不物理删除记录
     */
    @Update("UPDATE flow_instance SET del_flag = 1 WHERE instance_id = #{instanceId}")
    int deleteLogicById(@Param("instanceId") Long instanceId);

    // ====================5.查询我发起的审批列表 ====================
    /**
     * 查询【我发起的】审批列表，按发起时间倒序
     * @param initiatorId 发起人用户ID
     * @param delFlag 删除标记，业务传0查询有效数据
     */
    @Select("SELECT instance_id, def_id, flow_code, title, initiator_id, dept_id, form_data, current_order, status, del_flag, create_time, update_time " +
            "FROM flow_instance " +
            "WHERE initiator_id = #{initiatorId} AND del_flag = #{delFlag} " +
            "ORDER BY create_time DESC")
    List<FlowInstance> selectMyInitiateList(@Param("initiatorId") Long initiatorId,
                                            @Param("delFlag") Integer delFlag);

    // ====================6.多条件后台列表查询（动态script） ====================
    /**
     * 后台管理列表条件查询：流程编码、状态、发起人
     */
    @Select("<script>" +
            "SELECT instance_id, def_id, flow_code, title, initiator_id, dept_id, form_data, current_order, status, del_flag, create_time, update_time " +
            "FROM flow_instance " +
            "<where>" +
            "   del_flag = #{delFlag}" +
            "   <if test='flowCode != null and flowCode != \"\"'>" +
            "       AND flow_code = #{flowCode}" +
            "   </if>" +
            "   <if test='status != null'>" +
            "       AND status = #{status}" +
            "   </if>" +
            "   <if test='initiatorId != null'>" +
            "       AND initiator_id = #{initiatorId}" +
            "   </if>" +
            "</where>" +
            "ORDER BY create_time DESC" +
            "</script>")
    List<FlowInstance> selectList(@Param("flowCode") String flowCode,
                                  @Param("status") Integer status,
                                  @Param("initiatorId") Long initiatorId,
                                  @Param("delFlag") Integer delFlag);

}