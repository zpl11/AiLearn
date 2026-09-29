package org.example.componeyoa.service;

import org.example.componeyoa.entity.SysMenu;

import java.util.List;

public interface SysMenuService {
    /**
     * 创建路由权限
     *
     * @param sysMenu 菜单/权限信息
     * @return 是否创建成功
     */
    boolean createMenu(SysMenu sysMenu);

    /**
     * 删除路由权限
     *
     * @param menuId 菜单ID
     * @return 是否删除成功
     */
    boolean deleteMenu(Long menuId);

    /**
     * 修改路由权限
     *
     * @param sysMenu 菜单/权限信息
     * @return 是否修改成功
     */
    boolean updateMenu(SysMenu sysMenu);

    /**
     * 查询单个路由权限详情
     *
     * @param menuId 菜单ID
     * @return 菜单详情
     */
    SysMenu getMenuById(Long menuId);

    /**
     * 查询全部路由权限（用于后台菜单管理表格或授权分配权限树）
     *
     * @return 平铺菜单列表
     */
    List<SysMenu> listAllMenus();

    /**
     * 根据路由权限名称模糊查询
     *
     * @param menuName 菜单名称关键字
     * @return 匹配的菜单列表
     */
    List<SysMenu> searchMenusByName(String menuName);

    /**
     * 根据用户 ID 查询该用户拥有的动态菜单路由树（核心业务）
     * 过滤掉按钮(F)，仅保留目录(M)和菜单(C)，并在 Service 内部组装成层级树
     *
     * @param userId 用户ID
     * @return 具有层级结构的菜单树
     */
    List<SysMenu> listMenuTreeByUserId(Long userId);

    /**
     * 根据用户 ID 查询所有按钮权限标识（用于接口防刷鉴权与前端 v-hasPermi 指令）
     *
     * @param userId 用户ID
     * @return 权限标识集合 (例如: ["system:role:add", "system:user:delete"])
     */
    List<String> listPermsByUserId(Long userId);

    /**
     * 校验菜单下是否存在子节点（前置业务校验）
     *
     * @param menuId 菜单ID
     * @return 是否存在子菜单或按钮
     */
    boolean hasChildren(Long menuId);
}
