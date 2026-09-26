package org.example.componeyoa.controller;

import org.example.componeyoa.common.Result;
import org.example.componeyoa.entity.SysPost;
import org.example.componeyoa.service.SysPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/post") // 推荐加统一前缀，与部门、员工接口风格对齐
@CrossOrigin
public class SysPostController {

    @Autowired
    private SysPostService postService;

    // 1. 新增岗位
    @PostMapping("/addPost")
    public Result<Void> addPost(@RequestBody SysPost sysPost) {
        try {
            boolean success = postService.addPost(sysPost);
            // 三元表达式：成则 success，败则返回业务错误提示
            return success ? Result.success() : Result.error("新增岗位失败，岗位编码已存在或参数缺失");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 2. 删除岗位
    @DeleteMapping("/delPost")
    public Result<Void> delPost(@RequestParam("postId") Long postId) {
        try {
            boolean success = postService.delPost(postId);
            return success ? Result.success() : Result.error("删除岗位失败，数据不存在");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 3. 修改岗位
    @PutMapping("/updatePost")
    public Result<Void> updatePost(@RequestBody SysPost sysPost) {
        try {
            boolean success = postService.updatePost(sysPost);
            return success ? Result.success() : Result.error("修改岗位失败，未找到对应记录");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 4. 查询全部岗位
    @GetMapping("/queryAllPost")
    public Result<List<SysPost>> queryAllPost() {
        try {
            List<SysPost> listSysPost = postService.queryAllPost();
            return Result.success(listSysPost);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 5. 按名称模糊查询岗位
    @GetMapping("/queryPostByPostName")
    public Result<List<SysPost>> queryPostByPostName(@RequestParam(value = "postName", required = false) String postName) {
        try {
            List<SysPost> listSysPost = postService.queryPostByPostName(postName);
            return Result.success(listSysPost);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}