package org.example.componeyoa.controller;

import org.example.componeyoa.common.Result;
import org.example.componeyoa.entity.SysRole;
import org.example.componeyoa.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/role")
@CrossOrigin
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    /**
     * 查询角色列表（支持按角色名称和状态模糊搜索）
     */
    @GetMapping("/list")
    public Result<List<SysRole>> list(
            @RequestParam(value = "roleName", required = false) String roleName,
            @RequestParam(value = "status", required = false) Integer status) {
        List<SysRole> list = sysRoleService.listRoles(roleName, status);
        return Result.success(list);
    }

    /**
     * 根据角色 ID 获取角色详细信息
     */
    @GetMapping("/{roleId}")
    public Result<SysRole> getInfo(@PathVariable("roleId") Long roleId) {
        SysRole role = sysRoleService.getRoleById(roleId);
        if (role == null) {
            return Result.error("未找到对应的角色信息");
        }
        return Result.success(role);
    }

    /**
     * 根据角色 ID 查询已分配的菜单/权限 ID 集合（用于角色授权弹窗的树节点回显）
     */
    @GetMapping("/{roleId}/menuIds")
    public Result<List<Long>> getRoleMenuIds(@PathVariable("roleId") Long roleId) {
        List<Long> menuIds = sysRoleService.listMenuIdsByRoleId(roleId);
        return Result.success(menuIds);
    }

    /**
     * 新增角色
     */
    @PostMapping
    public Result<Void> add(@RequestBody SysRole role) {
        try {
            boolean success = sysRoleService.createRole(role);
            if (success) {
                return Result.success();
            }
            return Result.error("新增角色失败");
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改角色基本信息
     */
    @PutMapping
    public Result<Void> edit(@RequestBody SysRole role) {
        try {
            boolean success = sysRoleService.updateRole(role);
            if (success) {
                return Result.success();
            }
            return Result.error("修改角色失败");
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 分配角色菜单权限（核心授权接口）
     * 接收角色 ID 和选中的菜单 ID 数组
     */
    @PutMapping("/{roleId}/menus")
    public Result<Void> assignMenus(
            @PathVariable("roleId") Long roleId,
            @RequestBody List<Long> menuIds) {
        try {
            boolean success = sysRoleService.assignMenus(roleId, menuIds);
            if (success) {
                return Result.success();
            }
            return Result.error("分配权限失败");
        } catch (Exception e) {
            return Result.error("分配权限异常: " + e.getMessage());
        }
    }

    /**
     * 修改角色状态（启用/停用）
     */
    @PutMapping("/changeStatus")
    public Result<Void> changeStatus(@RequestBody SysRole role) {
        if (role.getRoleId() == null || role.getStatus() == null) {
            return Result.error("参数缺失：roleId 或 status 不能为空");
        }
        boolean success = sysRoleService.updateRoleStatus(role.getRoleId(), role.getStatus());
        if (success) {
            return Result.success();
        }
        return Result.error("修改角色状态失败");
    }

    /**
     * 删除角色（支持单选或批量）
     */
    @DeleteMapping("/{roleIds}")
    public Result<Void> remove(@PathVariable("roleIds") List<Long> roleIds) {
        try {
            boolean success = sysRoleService.deleteRoles(roleIds);
            if (success) {
                return Result.success();
            }
            return Result.error("删除角色失败");
        } catch (IllegalStateException e) {
            return Result.error(e.getMessage());
        }
    }
}