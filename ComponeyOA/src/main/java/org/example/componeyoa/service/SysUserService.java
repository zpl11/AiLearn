package org.example.componeyoa.service;

import org.example.componeyoa.entity.SysUser;

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
}
