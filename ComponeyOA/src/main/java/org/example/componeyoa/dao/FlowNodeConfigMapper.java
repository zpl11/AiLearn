package org.example.componeyoa.dao;

import org.apache.ibatis.annotations.*;
import org.example.componeyoa.entity.FlowNodeConfig;
import org.example.componeyoa.entity.vo.FlowNodeConfigVO;

import java.util.List;

/**
 * 流程节点配置 Mapper 接口
 */
@Mapper
public interface FlowNodeConfigMapper {

    /**
     * 根据流程定义ID查询全部节点配置链路
     * 关联岗位表 sys_post 查询 post_name，严格按照 node_order 升序排列
     *
     * @param defId 流程定义ID
     * @return 节点视图列表
     */
    @Select("SELECT c.node_id, c.def_id, c.node_name, c.node_order, c.post_id, c.approve_type, " +
            "c.create_time, c.update_time, p.post_name " +
            "FROM flow_node_config c " +
            "LEFT JOIN sys_post p ON c.post_id = p.post_id " +
            "WHERE c.def_id = #{defId} " +
            "ORDER BY c.node_order ASC")
    List<FlowNodeConfigVO> selectListByDefId(@Param("defId") Long defId);

    /**
     * 根据主键查询节点配置
     *
     * @param nodeId 节点配置ID
     * @return 节点实体
     */
    @Select("SELECT node_id, def_id, node_name, node_order, post_id, approve_type, create_time, update_time " +
            "FROM flow_node_config " +
            "WHERE node_id = #{nodeId}")
    FlowNodeConfig selectById(@Param("nodeId") Long nodeId);

    /**
     * 新增节点配置
     * 使用 useGeneratedKeys 回填主键 nodeId
     *
     * @param nodeConfig 节点实体
     * @return 受影响行数
     */
    @Insert("INSERT INTO flow_node_config(def_id, node_name, node_order, post_id, approve_type, create_time, update_time) " +
            "VALUES(#{defId}, #{nodeName}, #{nodeOrder}, #{postId}, #{approveType}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "nodeId")
    int insert(FlowNodeConfig nodeConfig);

    /**
     * 修改节点配置
     *
     * @param nodeConfig 节点实体
     * @return 受影响行数
     */
    @Update("UPDATE flow_node_config SET " +
            "node_name = #{nodeName}, " +
            "node_order = #{nodeOrder}, " +
            "post_id = #{postId}, " +
            "approve_type = #{approveType}, " +
            "update_time = NOW() " +
            "WHERE node_id = #{nodeId}")
    int update(FlowNodeConfig nodeConfig);

    /**
     * 根据主键删除节点配置
     *
     * @param nodeId 节点配置ID
     * @return 受影响行数
     */
    @Delete("DELETE FROM flow_node_config WHERE node_id = #{nodeId}")
    int deleteById(@Param("nodeId") Long nodeId);

    /**
     * 校验同一流程定义下，序号是否已存在
     * 排除 excludeNodeId 自身（用于修改时自身无需与自身比较）
     *
     * @param defId 流程定义ID
     * @param nodeOrder 节点顺序
     * @param excludeNodeId 需要排除的节点ID（新增传 0 或 null）
     * @return 冲突记录数
     */
    @Select("<script>" +
            "SELECT COUNT(1) FROM flow_node_config " +
            "WHERE def_id = #{defId} AND node_order = #{nodeOrder} " +
            "<if test='excludeNodeId != null and excludeNodeId != 0'>" +
            "  AND node_id != #{excludeNodeId} " +
            "</if>" +
            "</script>")
    int checkOrderUnique(@Param("defId") Long defId,
                         @Param("nodeOrder") Integer nodeOrder,
                         @Param("excludeNodeId") Long excludeNodeId);


}