package org.example.componeyoa.dao;

import org.apache.ibatis.annotations.*;
import org.example.componeyoa.entity.SysUserPost;

import java.util.List;

@Mapper
public interface SysUserPostMapper {

    /**
     * 单条建立员工与岗位绑定
     */
    @Insert("INSERT INTO sys_user_post(user_id, post_id) VALUES (#{userId}, #{postId})")
    int insertUserPost(SysUserPost sysUserPost);

    /**
     * 定点解除某员工与某特定岗位的绑定
     */
    @Delete("DELETE FROM sys_user_post WHERE user_id = #{userId} AND post_id = #{postId}")
    int deleteUserPost(SysUserPost sysUserPost);

    /**
     * 清空指定员工名下的所有岗位（换岗、重新分配或离职时使用）
     */
    @Delete("DELETE FROM sys_user_post WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 清空指定岗位下的所有员工（岗位被停用或删除时使用）
     */
    @Delete("DELETE FROM sys_user_post WHERE post_id = #{postId}")
    int deleteByPostId(@Param("postId") Long postId);

    /**
     * 查询所有绑定关系
     */
    @Select("SELECT user_id, post_id FROM sys_user_post")
    List<SysUserPost> queryAllUserPost();

    /**
     * 根据岗位 ID 查询关联的所有员工绑定记录（完整对象实体）
     */
    @Select("SELECT user_id, post_id FROM sys_user_post WHERE post_id = #{postId}")
    List<SysUserPost> queryUsersByPostId(@Param("postId") Long postId);

    /**
     * ✅【引擎核心新增】根据岗位 ID 直接查询该岗位下所有员工的 User_ID 集合
     * 用于工作流引擎派发待办任务时精准查找办理人
     */
    @Select("SELECT user_id FROM sys_user_post WHERE post_id = #{postId}")
    List<Long> selectUserIdsByPostId(@Param("postId") Long postId);

    /**
     * 根据员工 ID 查询关联的所有岗位绑定记录
     */
    @Select("SELECT user_id, post_id FROM sys_user_post WHERE user_id = #{userId}")
    List<SysUserPost> queryPostsByUserId(@Param("userId") Long userId);

    /**
     * 查询员工名下的岗位 ID 列表（专用于前端多选下拉框的回显）
     */
    @Select("SELECT post_id FROM sys_user_post WHERE user_id = #{userId}")
    List<Long> selectPostIdsByUserId(@Param("userId") Long userId);
}