package org.example.componeyoa.service.impl;

import org.example.componeyoa.dao.SysMenuMapper;
import org.example.componeyoa.entity.SysMenu;
import org.example.componeyoa.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl implements SysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createMenu(SysMenu sysMenu) {
        if (sysMenu == null) {
            return false;
        }
        // 如果未指定父级 ID，默认归为根目录（0L）
        if (sysMenu.getParentId() == null) {
            sysMenu.setParentId(0L);
        }
        return sysMenuMapper.insertMenu(sysMenu) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMenu(Long menuId) {
        if (menuId == null || menuId <= 0) {
            return false;
        }
        // 删除前置校验：若存在子节点或下属按钮，阻止删除，防止产生孤儿数据
        if (hasChildren(menuId)) {
            throw new IllegalStateException("该菜单下存在子菜单或按钮权限，禁止直接删除！");
        }
        return sysMenuMapper.deleteMenuById(menuId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMenu(SysMenu sysMenu) {
        if (sysMenu == null || sysMenu.getMenuId() == null) {
            return false;
        }
        // 防死循环基本校验：禁止将自身设为父节点
        if (Objects.equals(sysMenu.getMenuId(), sysMenu.getParentId())) {
            throw new IllegalArgumentException("修改失败，上级菜单不能选择自身！");
        }
        return sysMenuMapper.updateMenu(sysMenu) > 0;
    }

    @Override
    public SysMenu getMenuById(Long menuId) {
        if (menuId == null || menuId <= 0) {
            return null;
        }
        return sysMenuMapper.selectMenuById(menuId);
    }

    @Override
    public List<SysMenu> listAllMenus() {
        List<SysMenu> list = sysMenuMapper.selectAllMenus();
        return list != null ? list : Collections.emptyList();
    }

    @Override
    public List<SysMenu> searchMenusByName(String menuName) {
        List<SysMenu> list = sysMenuMapper.selectMenuList(menuName, null);
        return list != null ? list : Collections.emptyList();
    }

    @Override
    public List<SysMenu> listMenuTreeByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            return Collections.emptyList();
        }

        // 1. 联表查询该用户具备权限的平铺菜单（仅 M 目录与 C 页面）
        List<SysMenu> menuList = sysMenuMapper.selectRoutersByUserId(userId);
        if (menuList == null || menuList.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 以根目录 (parentId = 0L) 为起点递归构建多叉树
        return buildMenuTree(menuList, 0L);
    }

    @Override
    public List<String> listPermsByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            return Collections.emptyList();
        }
        List<String> perms = sysMenuMapper.selectPermsByUserId(userId);
        return perms != null ? perms : Collections.emptyList();
    }

    @Override
    public boolean hasChildren(Long menuId) {
        if (menuId == null || menuId <= 0) {
            return false;
        }
        return sysMenuMapper.countChildrenByParentId(menuId) > 0;
    }

    // ==================== 内部私有方法：递归组装树形菜单 ====================

    /**
     * Java 8 Stream 递归构建菜单树
     *
     * @param menuList 平铺菜单集合
     * @param parentId 当前处理层级的父 ID
     * @return 组装好的树形列表
     */
    private List<SysMenu> buildMenuTree(List<SysMenu> menuList, Long parentId) {
        return menuList.stream()
                .filter(menu -> Objects.equals(menu.getParentId(), parentId))
                .peek(menu -> menu.setChildren(buildMenuTree(menuList, menu.getMenuId())))
                .collect(Collectors.toList());
    }
}