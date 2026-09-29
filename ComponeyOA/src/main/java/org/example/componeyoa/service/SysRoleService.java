package org.example.componeyoa.service;

import org.example.componeyoa.entity.SysRole;

import java.util.List;

public interface SysRoleService {

    /**
     * 创建角色
     *
     * @param sysRole 角色实体信息
     * @return 是否创建成功
     */
    boolean createRole(SysRole sysRole);

    /**
     * 单个删除角色（需校验是否关联在职员工）
     *
     * @param roleId 角色ID
     * @return 是否删除成功
     */
    boolean deleteRole(Long roleId);

    /**
     * 批量删除角色
     *
     * @param roleIds 角色ID集合
     * @return 是否删除成功
     */
    boolean deleteRoles(List<Long> roleIds);

    /**
     * 修改角色基本信息
     *
     * @param sysRole 角色实体信息
     * @return 是否修改成功
     */
    boolean updateRole(SysRole sysRole);

    /**
     * 单独修改角色状态（启用/停用）
     *
     * @param roleId 角色ID
     * @param status 状态值 (0正常 1停用)
     * @return 是否修改成功
     */
    boolean updateRoleStatus(Long roleId, Integer status);

    /**
     * 根据角色 ID 查询单个角色详情
     *
     * @param roleId 角色ID
     * @return 角色详情对象
     */
    SysRole getRoleById(Long roleId);

    /**
     * 查询角色列表（支持按名称和状态组合筛选）
     *
     * @param roleName 角色名称关键字（可为空）
     * @param status   状态（可为空）
     * @return 角色列表
     */
    List<SysRole> listRoles(String roleName, Integer status);

    /**
     * 获取指定角色已分配的菜单/权限 ID 集合
     * 用于前端授权弹窗树节点的选中状态回显
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> listMenuIdsByRoleId(Long roleId);

    /**
     * 为角色分配菜单权限（维护 sys_role_menu 中间表）
     *
     * @param roleId  角色ID
     * @param menuIds 勾选的菜单ID集合
     * @return 是否分配成功
     */
    boolean assignMenus(Long roleId, List<Long> menuIds);
}