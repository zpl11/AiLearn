package org.example.componeyoa.service;

import org.example.componeyoa.entity.SysUser;
import org.example.componeyoa.entity.dto.UserRoleDTO;

import java.util.List;

public interface SysUserService {
    // 新增用户
    boolean createSysUser(SysUser sysUser);

    // 删除用户
    boolean delSysUser(Long userId);

    // 修改用户
    boolean updateSysUser(SysUser sysUser);

    // 查询全部用户
    List<SysUser> queryAllSysUser();

    // 根据部门 id 查询用户
    List<SysUser> queryUserByDeptId(Long deptId);

    // 根据用户 id 查询用户信息
    SysUser queryUserByUserId(Long userId);

    /**
     * 为用户分配角色
     *
     * @param dto 用户角色分配参数载荷
     * @return 是否分配成功
     */
    boolean assignUserRoles(UserRoleDTO dto);

    /**
     * 根据用户 ID 获取其已分配的角色 ID 集合（回显用）
     *
     * @param userId 目标用户ID
     * @return 角色ID列表
     */
    List<Long> listRoleIdsByUserId(Long userId);
}
