package org.example.componeyoa.controller;

import org.example.componeyoa.common.Result;
import org.example.componeyoa.entity.SysDept;
import org.example.componeyoa.service.SysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dept")
@CrossOrigin
public class SysDeptController {

    // 先将 Service 引用进来
    @Autowired
    private SysDeptService deptService;


    @GetMapping("/list")
    public Result<List<SysDept>> list(){
        List<SysDept> list = deptService.queryDeptList();
        return Result.success(list);
    }

    @GetMapping("/{deptId}")
    public Result<SysDept> getInfo(@PathVariable("deptId") Long deptId){
        SysDept dept = deptService.queryDeptById(deptId);
        return Result.success(dept);
    }

    @PostMapping
    public Result<Void> add(@RequestBody SysDept dept){
        try{
            deptService.addDept(dept);
            return Result.success();
        }catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping
    public Result<Void> edit(@RequestBody SysDept dept){
        try{
            deptService.updateDept(dept);
            return Result.success();
        }catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{deptId}")
    public Result<Void> remove(@PathVariable("deptId") Long deptId) {
        try{
            deptService.deleteDeptById(deptId);
            return Result.success();
        }catch(RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

}
