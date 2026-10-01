package org.example.componeyoa.dao;

import org.apache.ibatis.annotations.*;
import org.example.componeyoa.entity.FlowDefinition;

import java.util.List;
/**
 * 流程定义模板表 数据访问层 (DAO) - 纯注解驱动
 */
@Mapper
public interface FlowDefinitionMapper {

    /**
     * 1. 新增流程定义模板
     * 通过 @Options 实现主键自动回填至 defId
     */
    @Insert("INSERT INTO flow_definition ( " +
            "  flow_code, flow_name, status, del_flag, remark, create_by, create_time " +
            ") VALUES ( " +
            "  #{flowCode}, #{flowName}, IFNULL(#{status}, 0), 0, #{remark}, #{createBy}, NOW() " +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "defId")
    int insertFlowDefinition(FlowDefinition flowDefinition);

    /**
     * 2. 根据主键 ID 查询流程定义 (带逻辑删除过滤)
     */
    @Select("SELECT def_id, flow_code, flow_name, status, del_flag, remark, " +
            "create_by, create_time, update_by, update_time " +
            "FROM flow_definition " +
            "WHERE def_id = #{defId} AND del_flag = 0")
    FlowDefinition selectFlowDefinitionById(@Param("defId") Long defId);

    /**
     * 3. 根据唯一编码查询 (核心：用于 Service 层新增或修改时的排重校验)
     */
    @Select("SELECT def_id, flow_code, flow_name, status, del_flag, remark, " +
            "create_by, create_time, update_by, update_time " +
            "FROM flow_definition " +
            "WHERE flow_code = #{flowCode} AND del_flag = 0 " +
            "LIMIT 1")
    FlowDefinition selectFlowDefinitionByCode(@Param("flowCode") String flowCode);

    /**
     * 4. 条件多维度动态查询列表 (支持模糊搜索、状态筛选，纯注解中用 <script> 标签包裹)
     */
    @Select("<script>" +
            "SELECT def_id, flow_code, flow_name, status, del_flag, remark, " +
            "create_by, create_time, update_by, update_time " +
            "FROM flow_definition " +
            "<where> " +
            "  AND del_flag = 0 " +
            "  <if test=\"flowCode != null and flowCode != ''\"> " +
            "    AND flow_code LIKE CONCAT('%', #{flowCode}, '%') " +
            "  </if> " +
            "  <if test=\"flowName != null and flowName != ''\"> " +
            "    AND flow_name LIKE CONCAT('%', #{flowName}, '%') " +
            "  </if> " +
            "  <if test=\"status != null\"> " +
            "    AND status = #{status} " +
            "  </if> " +
            "</where> " +
            "ORDER BY create_time DESC " +
            "</script>")
    List<FlowDefinition> selectFlowDefinitionList(FlowDefinition flowDefinition);

    /**
     * 5. 动态修改 (使用 <set> 自动剔除末尾多余逗号)
     */
    @Update("<script>" +
            "UPDATE flow_definition " +
            "<set> " +
            "  <if test=\"flowCode != null and flowCode != ''\">flow_code = #{flowCode}, </if> " +
            "  <if test=\"flowName != null and flowName != ''\">flow_name = #{flowName}, </if> " +
            "  <if test=\"status != null\">status = #{status}, </if> " +
            "  <if test=\"remark != null\">remark = #{remark}, </if> " +
            "  <if test=\"updateBy != null and updateBy != ''\">update_by = #{updateBy}, </if> " +
            "  update_time = NOW() " +
            "</set> " +
            "WHERE def_id = #{defId} AND del_flag = 0 " +
            "</script>")
    int updateFlowDefinition(FlowDefinition flowDefinition);

    /**
     * 6. 逻辑删除流程定义 (将 del_flag 置为 1)
     */
    @Update("UPDATE flow_definition " +
            "SET del_flag = 1, update_time = NOW() " +
            "WHERE def_id = #{defId}")
    int deleteFlowDefinitionById(@Param("defId") Long defId);
}
