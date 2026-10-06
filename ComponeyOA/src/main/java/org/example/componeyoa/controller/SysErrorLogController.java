package org.example.componeyoa.controller;

import org.example.componeyoa.common.PageResult;
import org.example.componeyoa.common.Result;
import org.example.componeyoa.entity.SysErrorLog;
import org.example.componeyoa.entity.dto.SysErrorLogQueryDTO;
import org.example.componeyoa.service.SysErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/system/errorlog")
@CrossOrigin
public class SysErrorLogController {

    @Autowired
    private SysErrorLogService errorLogService;

    /**
     * 分页查询系统异常监控日志
     */
    @GetMapping("/list")
    public Result<PageResult<SysErrorLog>> list(SysErrorLogQueryDTO queryDTO) {
        try {
            PageResult<SysErrorLog> pageResult = errorLogService.queryErrorLogPage(queryDTO);
            return Result.success(pageResult);
        } catch (Exception e) {
            return Result.error("查询异常监控日志失败: " + e.getMessage());
        }
    }

    /**
     * 根据 ID 获取异常详情（含完整调用栈）
     */
    @GetMapping("/{errorId}")
    public Result<SysErrorLog> getDetail(@PathVariable("errorId") Long errorId) {
        try {
            SysErrorLog errorLog = errorLogService.queryErrorLogById(errorId);
            if (errorLog == null) {
                return Result.error("未找到对应异常日志");
            }
            return Result.success(errorLog);
        } catch (Exception e) {
            return Result.error("获取异常日志详情失败: " + e.getMessage());
        }
    }

    /**
     * 批量或单条删除异常日志
     */
    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestParam("errorIds") String errorIds) {
        try {
            if (errorIds == null || errorIds.trim().isEmpty()) {
                return Result.error("请选择要删除的异常记录");
            }
            List<Long> idList = Arrays.stream(errorIds.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            boolean success = errorLogService.deleteErrorLogByIds(idList);
            return success ? Result.success() : Result.error("删除异常日志失败");
        } catch (Exception e) {
            return Result.error("删除异常日志失败: " + e.getMessage());
        }
    }

    /**
     * 一键清空系统异常监控日志
     */
    @DeleteMapping("/clean")
    public Result<Void> clean() {
        try {
            errorLogService.cleanErrorLog();
            return Result.success();
        } catch (Exception e) {
            return Result.error("清空异常监控日志失败: " + e.getMessage());
        }
    }
}
