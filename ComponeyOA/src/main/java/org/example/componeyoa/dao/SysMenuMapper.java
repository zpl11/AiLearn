package org.example.componeyoa.dao;

import org.apache.ibatis.annotations.*;
import org.example.componeyoa.entity.SysMenu;

import java.util.List;

@Mapper
public interface SysMenuMapper {

    // ==================== 1. 新增菜单/权限 ====================
    /**
     * 新增菜单路由或按钮权限
     * useGeneratedKeys 回填自增主键 menuId
     */
    @Insert("INSERT INTO sys_menu (" +
            "parent_id, menu_name, order_num, path, component, menu_type, perms, status, create_by, create_time, update_by, update_time" +
            ") VALUES (" +
            "#{parentId}, #{menuName}, #{orderNum}, #{path}, #{component}, #{menuType}, #{perms}, #{status}, #{createBy}, NOW(), #{updateBy}, NOW()" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "menuId")
    int insertMenu(SysMenu sysMenu);


    // ==================== 2. 删除菜单/权限 ====================
    /**
     * 根据主键物理删除菜单
     */
    @Delete("DELETE FROM sys_menu WHERE menu_id = #{menuId}")
    int deleteMenuById(@Param("menuId") Long menuId);

    /**
     * 查询某菜单下是否存在子菜单或按钮
     * 用于删除前的防误删前置校验：如果返回值 > 0，禁止直接删除父节点
     */
    @Select("SELECT COUNT(1) FROM sys_menu WHERE parent_id = #{menuId}")
    int countChildrenByParentId(@Param("menuId") Long menuId);


    // ==================== 3. 修改菜单/权限 ====================
    /**
     * 根据主键修改菜单基本信息
     */
    @Update("UPDATE sys_menu SET " +
            "parent_id = #{parentId}, " +
            "menu_name = #{menuName}, " +
            "order_num = #{orderNum}, " +
            "path = #{path}, " +
            "component = #{component}, " +
            "menu_type = #{menuType}, " +
            "perms = #{perms}, " +
            "status = #{status}, " +
            "update_by = #{updateBy}, " +
            "update_time = NOW() " +
            "WHERE menu_id = #{menuId}")
    int updateMenu(SysMenu sysMenu);


    // ==================== 4. 查询菜单详情与列表 ====================
    /**
     * 根据主键查询单个菜单详情
     */
    @Select("SELECT menu_id, parent_id, menu_name, order_num, path, component, menu_type, perms, status, create_by, create_time, update_by, update_time " +
            "FROM sys_menu WHERE menu_id = #{menuId}")
    SysMenu selectMenuById(@Param("menuId") Long menuId);

    /**
     * 查询系统全部菜单列表（按排序号升序排列，供后台菜单管理表格以及权限树勾选使用）
     */
    @Select("SELECT menu_id, parent_id, menu_name, order_num, path, component, menu_type, perms, status, create_by, create_time, update_by, update_time " +
            "FROM sys_menu " +
            "ORDER BY order_num ASC")
    List<SysMenu> selectAllMenus();

    /**
     * 根据菜单名称或状态模糊筛选（动态 SQL 演示，适应搜索栏）
     */
    @Select("<script>" +
            "SELECT menu_id, parent_id, menu_name, order_num, path, component, menu_type, perms, status, create_by, create_time, update_by, update_time " +
            "FROM sys_menu " +
            "<where>" +
            "  <if test='menuName != null and menuName != \"\"'>" +
            "    AND menu_name LIKE CONCAT('%', #{menuName}, '%')" +
            "  </if>" +
            "  <if test='status != null'>" +
            "    AND status = #{status}" +
            "  </if>" +
            "</where>" +
            "ORDER BY order_num ASC" +
            "</script>")
    List<SysMenu> selectMenuList(@Param("menuName") String menuName, @Param("status") Integer status);


    // ==================== 5. 鉴权与动态路由专属查询（核心） ====================
    /**
     * 根据用户 ID 联表查询该用户拥有的所有可用菜单（目录 M 和 页面 C）
     * 关联链路：sys_user_role -> sys_role_menu -> sys_menu
     * 用于前端加载侧边栏与注册动态路由
     */
    @Select("SELECT DISTINCT m.menu_id, m.parent_id, m.menu_name, m.order_num, m.path, m.component, m.menu_type, m.perms, m.status " +
            "FROM sys_menu m " +
            "LEFT JOIN sys_role_menu rm ON m.menu_id = rm.menu_id " +
            "LEFT JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
            "LEFT JOIN sys_role r ON ur.role_id = r.role_id " +
            "WHERE ur.user_id = #{userId} " +
            "  AND m.menu_type IN ('M', 'C') " +
            "  AND m.status = 0 " +
            "  AND r.status = 0 " +
            "  AND r.del_flag = 0 " +
            "ORDER BY m.parent_id ASC, m.order_num ASC")
    List<SysMenu> selectRoutersByUserId(@Param("userId") Long userId);

    /**
     * 根据用户 ID 联表查询该用户拥有的所有按钮权限标识（perms）
     * 用于前端页面按钮级别的控制（v-hasPermi）以及后端接口鉴权拦截
     */
    @Select("SELECT DISTINCT m.perms " +
            "FROM sys_menu m " +
            "LEFT JOIN sys_role_menu rm ON m.menu_id = rm.menu_id " +
            "LEFT JOIN sys_user_role ur ON rm.role_id = ur.role_id " +
            "LEFT JOIN sys_role r ON ur.role_id = r.role_id " +
            "WHERE ur.user_id = #{userId} " +
            "  AND m.status = 0 " +
            "  AND r.status = 0 " +
            "  AND r.del_flag = 0 " +
            "  AND m.perms IS NOT NULL AND m.perms != ''")
    List<String> selectPermsByUserId(@Param("userId") Long userId);
}