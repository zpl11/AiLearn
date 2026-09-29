package org.example.componeyoa.controller;

import org.example.componeyoa.common.Result;
import org.example.componeyoa.entity.SysMenu;
import org.example.componeyoa.service.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/menu")
@CrossOrigin
public class SysMenuController {

    @Autowired
    private SysMenuService sysMenuService;

    /**
     * 获取当前登录用户的动态路由菜单树（构建前端左侧菜单栏）
     */
    @GetMapping("/getRouters/{userId}")
    public Result<List<SysMenu>> getRouters(@PathVariable("userId") Long userId) {
        List<SysMenu> menuTree = sysMenuService.listMenuTreeByUserId(userId);
        return Result.success(menuTree);
    }

    /**
     * 获取当前登录用户的按钮级别权限标识集合（前端 v-hasPermi 指令鉴权）
     */
    @GetMapping("/getPerms/{userId}")
    public Result<List<String>> getPerms(@PathVariable("userId") Long userId) {
        List<String> perms = sysMenuService.listPermsByUserId(userId);
        return Result.success(perms);
    }

    /**
     * 查询系统全部菜单列表（后台菜单管理列表，支持按名称模糊查询）
     */
    @GetMapping("/list")
    public Result<List<SysMenu>> list(@RequestParam(value = "menuName", required = false) String menuName) {
        List<SysMenu> list;
        if (menuName != null && !menuName.trim().isEmpty()) {
            list = sysMenuService.searchMenusByName(menuName.trim());
        } else {
            list = sysMenuService.listAllMenus();
        }
        return Result.success(list);
    }

    /**
     * 根据菜单 ID 获取详细信息（表单数据回显）
     */
    @GetMapping("/{menuId}")
    public Result<SysMenu> getInfo(@PathVariable("menuId") Long menuId) {
        SysMenu menu = sysMenuService.getMenuById(menuId);
        if (menu == null) {
            return Result.error("未找到对应的菜单信息");
        }
        return Result.success(menu);
    }

    /**
     * 新增菜单路由或权限按钮
     */
    @PostMapping
    public Result<Void> add(@RequestBody SysMenu menu) {
        boolean success = sysMenuService.createMenu(menu);
        if (success) {
            return Result.success();
        }
        return Result.error("新增菜单失败");
    }

    /**
     * 修改菜单路由或权限按钮
     */
    @PutMapping
    public Result<Void> edit(@RequestBody SysMenu menu) {
        try {
            boolean success = sysMenuService.updateMenu(menu);
            if (success) {
                return Result.success();
            }
            return Result.error("修改菜单失败");
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{menuId}")
    public Result<Void> remove(@PathVariable("menuId") Long menuId) {
        try {
            boolean success = sysMenuService.deleteMenu(menuId);
            if (success) {
                return Result.success();
            }
            return Result.error("删除菜单失败");
        } catch (IllegalStateException e) {
            return Result.error(e.getMessage());
        }
    }
}