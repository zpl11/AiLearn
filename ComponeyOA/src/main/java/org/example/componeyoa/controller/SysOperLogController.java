package org.example.componeyoa.controller;

import org.example.componeyoa.common.PageResult;
import org.example.componeyoa.common.Result;
import org.example.componeyoa.entity.SysOperLog;
import org.example.componeyoa.entity.dto.SysOperLogQueryDTO;
import org.example.componeyoa.service.SysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/system/operlog")
@CrossOrigin
public class SysOperLogController {

    @Autowired
    private SysOperLogService operLogService;

    /**
     * 分页多条件查询操作日志列表
     */
    @GetMapping("/list")
    public Result<PageResult<SysOperLog>> list(SysOperLogQueryDTO queryDTO) {
        try {
            PageResult<SysOperLog> pageResult = operLogService.queryOperLogPage(queryDTO);
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.error("查询操作日志失败: " + e.getMessage());
        }
    }

    /**
     * 根据日志主键查询单条完整详情
     */
    @GetMapping("/{operId}")
    public Result<SysOperLog> getDetail(@PathVariable("operId") Long operId) {
        try {
            SysOperLog operLog = operLogService.queryOperLogById(operId);
            if (operLog == null) {
                return Result.error("未找到对应日志记录");
            }
            return Result.success(operLog);
        } catch (Exception e) {
            return Result.error("获取操作日志详情失败: " + e.getMessage());
        }
    }

    /**
     * 批量或单条删除操作日志（入参支持以逗号分隔的 ID 列表，如 "1,2,3"）
     */
    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestParam("operIds") String operIds) {
        try {
            if (operIds == null || operIds.trim().isEmpty()) {
                return Result.error("请选择要删除的日志项");
            }
            List<Long> idList = Arrays.stream(operIds.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            boolean success = operLogService.deleteOperLogByIds(idList);
            return success ? Result.success() : Result.error("删除日志失败，未找到记录");
        } catch (Exception e) {
            return Result.error("删除操作日志失败: " + e.getMessage());
        }
    }

    /**
     * 一键清空所有操作日志
     */
    @DeleteMapping("/clean")
    public Result<Void> clean() {
        try {
            operLogService.cleanOperLog();
            return Result.success();
        } catch (Exception e) {
            return Result.error("清空操作日志失败: " + e.getMessage());
        }
    }
}
