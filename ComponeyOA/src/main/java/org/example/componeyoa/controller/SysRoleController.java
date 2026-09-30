package org.example.componeyoa.controller;

import org.example.componeyoa.common.Result;
import org.example.componeyoa.entity.dto.RoleMenuDTO;
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
     * 查询角色列表
     */
    @GetMapping("/list")
    public Result<List<SysRole>> list(
            @RequestParam(value = "roleName", required = false) String roleName,
            @RequestParam(value = "status", required = false) Integer status) {
        return Result.success(sysRoleService.listRoles(roleName, status));
    }

    /**
     * 获取角色详细信息
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
     * 查询角色已绑定的菜单权限ID列表（树回显）
     */
    @GetMapping("/{roleId}/menuIds")
    public Result<List<Long>> getRoleMenuIds(@PathVariable("roleId") Long roleId) {
        return Result.success(sysRoleService.listMenuIdsByRoleId(roleId));
    }

    /**
     * 新增角色
     */
    @PostMapping
    public Result<Void> add(@RequestBody SysRole role) {
        boolean success = sysRoleService.createRole(role);
        return success ? Result.success() : Result.error("新增角色失败");
    }

    /**
     * 修改角色
     */
    @PutMapping
    public Result<Void> edit(@RequestBody SysRole role) {
        boolean success = sysRoleService.updateRole(role);
        return success ? Result.success() : Result.error("修改角色失败");
    }

    /**
     * 分配角色菜单权限（使用规范 DTO 入参）
     */
    @PutMapping("/auth")
    public Result<Void> assignAuth(@RequestBody RoleMenuDTO dto) {
        boolean success = sysRoleService.assignRoleMenus(dto);
        return success ? Result.success() : Result.error("分配权限失败");
    }

    /**
     * 修改角色状态
     */
    @PutMapping("/changeStatus")
    public Result<Void> changeStatus(@RequestBody SysRole role) {
        if (role.getRoleId() == null || role.getStatus() == null) {
            return Result.error("参数缺失：roleId 或 status 不能为空");
        }
        boolean success = sysRoleService.updateRoleStatus(role.getRoleId(), role.getStatus());
        return success ? Result.success() : Result.error("修改角色状态失败");
    }

    /**
     * 批量删除角色
     */
    @DeleteMapping("/{roleIds}")
    public Result<Void> remove(@PathVariable("roleIds") List<Long> roleIds) {
        boolean success = sysRoleService.deleteRoles(roleIds);
        return success ? Result.success() : Result.error("删除角色失败");
    }
}