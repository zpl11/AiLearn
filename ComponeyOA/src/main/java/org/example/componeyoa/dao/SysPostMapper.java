package org.example.componeyoa.dao;

import org.apache.ibatis.annotations.*;
import org.example.componeyoa.entity.SysPost;

import java.util.List;

@Mapper
public interface SysPostMapper {

    // 1. 新增岗位（显式声明插入列名，通过 #{属性名} 自动映射实体属性）
    @Insert("INSERT INTO sys_post(post_code, post_name, post_sort, status, create_by) " +
            "VALUES(#{postCode}, #{postName}, #{postSort}, #{status}, #{createBy})")
    int addSysPost(SysPost sysPost);

    // 2. 逻辑删除（Update 语法，不带 INTO，将 del_flag 置为 1 并刷新修改时间）
    @Update("UPDATE sys_post " +
            "SET del_flag = 1, update_time = NOW() " +
            "WHERE post_id = #{postId}")
    int delSysPost(@Param("postId") Long postId);

    // 3. 修改岗位（传入整个实体对象，更新指定字段并刷新 update_time）
    @Update("UPDATE sys_post " +
            "SET post_code = #{postCode}, " +
            "    post_name = #{postName}, " +
            "    post_sort = #{postSort}, " +
            "    status = #{status}, " +
            "    update_time = NOW() " +
            "WHERE post_id = #{postId}")
    int updateSysPost(SysPost sysPost);

    // 4. 查询全部有效岗位（必须带上 del_flag = 0 过滤逻辑删除，并包含 post_id）
    @Select("SELECT post_id, post_code, post_name, post_sort, status, create_time " +
            "FROM sys_post " +
            "WHERE del_flag = 0 " +
            "ORDER BY post_sort ASC")
    List<SysPost> queryAllSysPost();

    // 5. 按岗位名称模糊查询（使用 MySQL 的 CONCAT 函数安全拼接 %，防止 SQL 注入）
    @Select("SELECT post_id, post_code, post_name, post_sort, status, create_time " +
            "FROM sys_post " +
            "WHERE del_flag = 0 AND post_name LIKE CONCAT('%', #{postName}, '%') " +
            "ORDER BY post_sort ASC")
    List<SysPost> queryByLikeSysPost(@Param("postName") String postName);

    // 6. 根据 ID 查询单条详情（用于编辑时的数据回显，统一使用 Long 类型的 postId）
    @Select("SELECT post_id, post_code, post_name, post_sort, status, create_time " +
            "FROM sys_post " +
            "WHERE post_id = #{postId} AND del_flag = 0")
    SysPost queryByIdSysPost(@Param("postId") Long postId);

    // 7. 根据 postCode 查询（提供给 Service 层做编码排重校验）
    @Select("SELECT post_id, post_code, post_name " +
            "FROM sys_post " +
            "WHERE post_code = #{postCode} AND del_flag = 0 LIMIT 1")
    SysPost selectByPostCode(@Param("postCode") String postCode);
}