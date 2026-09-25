package org.example.componeyoa.dao;

import org.apache.ibatis.annotations.*;
import org.example.componeyoa.entity.SysDept;

import java.util.List;

@Mapper
public interface SysDeptMapper {

    // 1. 全量查询：带出 order_num，优先按 order_num 升序排列
    @Select("SELECT dept_id, parent_id, parent_ids, dept_name, order_num, leader_id, status, del_flag, create_time, update_time " +
            "FROM sys_dept WHERE del_flag = 0 ORDER BY order_num ASC, dept_id ASC")
    List<SysDept> selectAll();

    // 2. 详情查询：带出 order_num
    @Select("SELECT dept_id, parent_id, parent_ids, dept_name, order_num, leader_id, status, del_flag, create_time, update_time " +
            "FROM sys_dept WHERE dept_id = #{deptId} AND del_flag = 0")
    SysDept selectById(@Param("deptId") Long deptId);

    // 3. 新增部门：写入 order_num
    @Insert("INSERT INTO sys_dept(parent_id, parent_ids, dept_name, order_num, leader_id, status, del_flag) " +
            "VALUES(#{parentId}, #{parentIds}, #{deptName}, #{orderNum}, #{leaderId}, #{status}, 0)")
    @Options(useGeneratedKeys = true, keyProperty = "deptId")
    int insertDept(SysDept dept);

    // 4. 修改部门：更新 order_num
    @Update("UPDATE sys_dept SET parent_id = #{parentId}, parent_ids = #{parentIds}, " +
            "dept_name = #{deptName}, order_num = #{orderNum}, leader_id = #{leaderId}, status = #{status} " +
            "WHERE dept_id = #{deptId}")
    int updateDept(SysDept dept);

    // 5. 逻辑删除
    @Update("UPDATE sys_dept SET del_flag = 1 WHERE dept_id = #{deptId}")
    int deleteDeptByID(@Param("deptId") Long deptId);

    // 6. 统计子级数量
    @Select("SELECT COUNT(1) FROM sys_dept WHERE parent_id = #{parentId} AND del_flag = 0")
    int countChildrenByParentId(@Param("parentId") Long parentId);
}