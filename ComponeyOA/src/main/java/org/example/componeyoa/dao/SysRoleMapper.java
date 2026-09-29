package org.example.componeyoa.dao;

import org.apache.ibatis.annotations.*;
import org.example.componeyoa.entity.SysRole;

import java.util.List;

@Mapper
public interface SysRoleMapper {

    // ==================== 1. 新增角色 ====================
    /**
     * 新增角色信息
     * 支持自增主键回填到 sysRole.roleId 中
     */
    @Insert("INSERT INTO sys_role (" +
            "role_name, role_code, role_sort, status, del_flag, remark, create_by, create_time, update_by, update_time" +
            ") VALUES (" +
            "#{roleName}, #{roleCode}, #{roleSort}, #{status}, 0, #{remark}, #{createBy}, NOW(), #{updateBy}, NOW()" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "roleId")
    int insertRole(SysRole sysRole);


    // ==================== 2. 删除角色 ====================
    /**
     * 逻辑删除角色（将 del_flag 改为 1）
     * 适配 Service 层的单参数调用
     */
    @Update("UPDATE sys_role SET del_flag = 1, update_time = NOW() WHERE role_id = #{roleId}")
    int deleteRoleById(@Param("roleId") Long roleId);

    /**
     * 物理删除角色（彻底清除数据库记录）
     */
    @Delete("DELETE FROM sys_role WHERE role_id = #{roleId}")
    int deleteRoleByIdPhysically(@Param("roleId") Long roleId);


    // ==================== 3. 修改角色 ====================
    /**
     * 根据主键修改角色基本信息
     */
    @Update("UPDATE sys_role SET " +
            "role_name = #{roleName}, " +
            "role_code = #{roleCode}, " +
            "role_sort = #{roleSort}, " +
            "status = #{status}, " +
            "remark = #{remark}, " +
            "update_by = #{updateBy}, " +
            "update_time = NOW() " +
            "WHERE role_id = #{roleId} AND del_flag = 0")
    int updateRole(SysRole sysRole);


    // ==================== 4. 查询角色 ====================
    /**
     * 根据主键查询单个角色详情
     */
    @Select("SELECT role_id, role_name, role_code, role_sort, status, del_flag, remark, create_by, create_time, update_by, update_time " +
            "FROM sys_role " +
            "WHERE role_id = #{roleId} AND del_flag = 0")
    SysRole selectRoleById(@Param("roleId") Long roleId);

    /**
     * 根据角色编码查询（用于 Service 层防重排重校验）
     */
    @Select("SELECT role_id, role_name, role_code, role_sort, status, del_flag, remark, create_by, create_time, update_by, update_time " +
            "FROM sys_role " +
            "WHERE role_code = #{roleCode} AND del_flag = 0 LIMIT 1")
    SysRole selectRoleByCode(@Param("roleCode") String roleCode);

    /**
     * 查询所有未被删除的角色（支持按排序字段和创建时间升序排列）
     */
    @Select("SELECT role_id, role_name, role_code, role_sort, status, del_flag, remark, create_by, create_time, update_by, update_time " +
            "FROM sys_role " +
            "WHERE del_flag = 0 " +
            "ORDER BY role_sort ASC, create_time DESC")
    List<SysRole> selectAllRoles();

    /**
     * 根据角色名称模糊匹配查询
     */
    @Select("SELECT role_id, role_name, role_code, role_sort, status, del_flag, remark, create_by, create_time, update_by, update_time " +
            "FROM sys_role " +
            "WHERE del_flag = 0 AND role_name LIKE CONCAT('%', #{roleName}, '%') " +
            "ORDER BY role_sort ASC")
    List<SysRole> selectRolesByName(@Param("roleName") String roleName);


    // ==================== 5. 角色与菜单关联维护 (sys_role_menu) ====================
    /**
     * 根据角色 ID 删除已分配的所有菜单权限
     */
    @Delete("DELETE FROM sys_role_menu WHERE role_id = #{roleId}")
    int deleteRoleMenusByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量为角色插入菜单权限（使用 script 动态标签拼接多行插入）
     */
    @Insert("<script>" +
            "INSERT INTO sys_role_menu (role_id, menu_id) VALUES " +
            "<foreach collection='menuIds' item='menuId' separator=','>" +
            "(#{roleId}, #{menuId})" +
            "</foreach>" +
            "</script>")
    int batchInsertRoleMenus(@Param("roleId") Long roleId, @Param("menuIds") List<Long> menuIds);

    /**
     * 根据角色 ID 查询已绑定的所有菜单 ID 集合（用于前端树形勾选回显）
     */
    @Select("SELECT menu_id FROM sys_role_menu WHERE role_id = #{roleId}")
    List<Long> selectMenuIdsByRoleId(@Param("roleId") Long roleId);


    // ==================== 6. 核心前置校验：用户与角色关联 (sys_user_role) ====================
    /**
     * 查询该角色下是否绑定了在职员工
     * 用于删除角色前的级联校验：返回值 > 0 时严禁删除
     */
    @Select("SELECT COUNT(1) FROM sys_user_role WHERE role_id = #{roleId}")
    int countUserRoleByRoleId(@Param("roleId") Long roleId);
}