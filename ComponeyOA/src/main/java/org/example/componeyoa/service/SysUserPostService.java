package org.example.componeyoa.service;

import org.example.componeyoa.entity.SysUserPost;
import java.util.List;

public interface SysUserPostService {

    /**
     * 为员工新增单个岗位
     */
    boolean createUserPost(SysUserPost sysUserPost);

    /**
     * 定点解除某个绑定关系
     */
    boolean deleteUserPost(SysUserPost sysUserPost);

    /**
     * 核心业务：更新/重置员工的岗位绑定关系（先删后插）
     *
     * @param userId  员工ID
     * @param postIds 该员工最新勾选的岗位ID集合
     * @return 是否分配成功
     */
    boolean updateUserPosts(Long userId, List<Long> postIds);

    /**
     * 查询所有绑定关系
     */
    List<SysUserPost> queryAllSysUserPost();

    /**
     * 查询某岗位下的所有员工关联
     */
    List<SysUserPost> queryAllSysUserByPost(Long postId);

    /**
     * 查询某员工名下的所有岗位关联
     */
    List<SysUserPost> queryAllPostByUser(Long userId);

    /**
     * 获取员工当前绑定的岗位ID列表（用于前端回显）
     */
    List<Long> getPostIdsByUserId(Long userId);
}