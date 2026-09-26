package org.example.componeyoa.controller;

import org.example.componeyoa.common.Result;
import org.example.componeyoa.entity.SysUser;
import org.example.componeyoa.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/user")
@CrossOrigin
public class SysUserController {

    @Autowired
    private SysUserService userService;

    // 1. 新增用户（接收 JSON 体，必须使用 @RequestBody）
    @PostMapping("/add")
    public Result<Void> addUser(@RequestBody SysUser sysUser) {
        boolean success = userService.createSysUser(sysUser);
        if (!success) {
            return Result.error("新增员工失败，登录账号已存在或必填项为空");
        }
        return Result.success();
    }

    // 2. 删除用户（路径传参或 URL 参数均可）
    @DeleteMapping("/del")
    public Result<Void> delUser(@RequestParam("userId") Long userId) {
        boolean success = userService.delSysUser(userId);
        if (!success) {
            return Result.error("删除员工失败");
        }
        return Result.success();
    }

    // 3. 修改用户（接收 JSON 体）
    @PutMapping("/update")
    public Result<Void> updateUser(@RequestBody SysUser sysUser) {
        boolean success = userService.updateSysUser(sysUser);
        if (!success) {
            return Result.error("修改员工失败，员工不存在");
        }
        return Result.success();
    }

    // 4. 查询全部用户
    @GetMapping("/list")
    public Result<List<SysUser>> getAllUser() {
        List<SysUser> userList = userService.queryAllSysUser();
        return Result.success(userList);
    }

    // 5. 根据部门 ID 查询用户（部门树联动，Controller 直接使用 Long 接收）
    @GetMapping("/byDept")
    public Result<List<SysUser>> queryUserByDeptId(@RequestParam("deptId") Long deptId) {
        List<SysUser> userList = userService.queryUserByDeptId(deptId);
        return Result.success(userList);
    }
}