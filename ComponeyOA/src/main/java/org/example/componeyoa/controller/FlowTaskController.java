package org.example.componeyoa.controller;

import org.example.componeyoa.common.Result;
import org.example.componeyoa.entity.dto.ProcessTaskDTO;
import org.example.componeyoa.entity.vo.FlowTaskVO;
import org.example.componeyoa.service.FlowTaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 审批中心 - 我的任务控制器
 * 职责边界：只做协议分发与参数接发，不做厚重业务逻辑
 */
@RestController
@RequestMapping("/system/task")
@CrossOrigin
public class FlowTaskController {

    private final FlowTaskService flowTaskService;

    public FlowTaskController(FlowTaskService flowTaskService) {
        this.flowTaskService = flowTaskService;
    }

    /**
     * 查询我的待办列表
     * 规范动词：@GetMapping 查
     */
    @GetMapping("/todo")
    public Result<List<FlowTaskVO>> getMyTodo() {
        // 模拟从上下文或 Token 拦截器中获取当前登录用户的 userId
        // （对应模块1中的 ThreadLocal 或 JWT 提取机制）[cite: 3]
        Long currentUserId = 1L;

        List<FlowTaskVO> list = flowTaskService.getMyTodoList(currentUserId);

        // 使用 Result<List<FlowTaskVO>> 的方式来返回 data 中的数据列表[cite: 1]
        return Result.success(list);
    }

    /**
     * 查询我的已办列表
     * 规范动词：@GetMapping 查[cite: 1]
     * 补充说明：如果是 URL 上的简单参数（如 ?status=1），则使用 @RequestParam[cite: 1]
     */
    @GetMapping("/done")
    public Result<List<FlowTaskVO>> getMyDone(@RequestParam(value = "status", required = false) Integer status) {
        // 同理，获取当前登录用户
        Long currentUserId = 1L;

        List<FlowTaskVO> list = flowTaskService.getMyDoneList(currentUserId, status);
        return Result.success(list);
    }

    /**
     * 审批处理（同意/驳回）
     * 规范动词：是对数据的修改，所以使用 @PutMapping[cite: 1]
     * 补充说明：JSON 请求体必须加 @RequestBody 注解，框架才会调用 Jackson 反序列化为 Java 对象[cite: 1]
     */
    @PutMapping("/process")
    public Result<Void> processTask(@RequestBody ProcessTaskDTO dto) {
        // 调用 Service 层业务，Service 层依据 rows > 0 返回 true/false[cite: 1]
        boolean success = flowTaskService.processTask(dto.getTaskId(), dto.getStatus(), dto.getComment());

        if (success) {
            // Result<Void> 这里包裹的泛型就是返回值 data 中的泛型，不需要返回数据时 data 中就不要放东西了[cite: 1]
            return Result.success();
        } else {
            return Result.error("审批处理失败，请刷新后重试");
        }
    }
}
