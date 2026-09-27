package org.example.componeyoa.controller;

import org.example.componeyoa.common.Result;
import org.example.componeyoa.entity.SysUserPost;
import org.example.componeyoa.entity.dto.UserPostAssignDTO;
import org.example.componeyoa.service.SysUserPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/user-post")
@CrossOrigin
public class SysUserPostController {

    @Autowired
    private SysUserPostService sysUserPostService;

    // 增：单条绑定
    @PostMapping
    public Result<Void> addSysUserPost(@RequestBody SysUserPost sysUserPost) {
        try {
            boolean success = sysUserPostService.createUserPost(sysUserPost);
            return success ? Result.success() : Result.error("绑定失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // 删：单条定点解绑
    @DeleteMapping
    public Result<Void> delSysUserPost(@RequestBody SysUserPost sysUserPost) {
        try {
            boolean success = sysUserPostService.deleteUserPost(sysUserPost);
            return success ? Result.success() : Result.error("解绑失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // 改：为员工重置岗位（先删后插），通过 JSON 接收参数
    @PutMapping("/assign")
    public Result<Void> assignUserPosts(@RequestBody UserPostAssignDTO dto) {
        try {
            if (dto.getUserId() == null) {
                return Result.error("用户ID不能为空");
            }
            boolean success = sysUserPostService.updateUserPosts(dto.getUserId(), dto.getPostIds());
            return success ? Result.success() : Result.error("分配岗位失败");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // 查全部绑定列表
    @GetMapping("/list")
    public Result<List<SysUserPost>> queryAllSysUserPost() {
        try {
            List<SysUserPost> list = sysUserPostService.queryAllSysUserPost();
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // 根据岗位查询所有员工（注意参数名小写 postId）
    @GetMapping("/users/{postId}")
    public Result<List<SysUserPost>> queryAllSysUserByPost(@PathVariable("postId") Long postId) {
        try {
            List<SysUserPost> list = sysUserPostService.queryAllSysUserByPost(postId);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // 根据员工查询绑定的所有岗位ID（用于前端 el-select 回显）
    @GetMapping("/post-ids/{userId}")
    public Result<List<Long>> queryPostIdsByUser(@PathVariable("userId") Long userId) {
        try {
            List<Long> list = sysUserPostService.getPostIdsByUserId(userId);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}