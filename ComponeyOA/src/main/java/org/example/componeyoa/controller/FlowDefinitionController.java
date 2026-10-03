package org.example.componeyoa.controller;

import org.example.componeyoa.common.Result;
import org.example.componeyoa.entity.FlowDefinition;
import org.example.componeyoa.service.FlowDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 审批中心 - 流程定义模板 控制层
 */
@RestController
@RequestMapping("/flow/definition")
@CrossOrigin
public class FlowDefinitionController {

    @Autowired
    private FlowDefinitionService flowDefinitionService;

    /**
     * 1. 新增流程定义模板
     * 请求方式: POST
     * 路径: /flow/definition
     */
    @PostMapping
    public Result<Void> add(@RequestBody FlowDefinition flowDefinition) {
        if (flowDefinition.getFlowCode() == null || flowDefinition.getFlowCode().trim().isEmpty()) {
            return Result.error("流程编码不能为空");
        }
        if (flowDefinition.getFlowName() == null || flowDefinition.getFlowName().trim().isEmpty()) {
            return Result.error("流程名称不能为空");
        }

        boolean success = flowDefinitionService.addFlowDefinition(flowDefinition);
        if (!success) {
            return Result.error("新增流程失败，流程编码可能已存在");
        }
        return Result.success();
    }

    /**
     * 2. 查询流程定义列表 (支持条件搜索)
     * 请求方式: GET
     * 路径: /flow/definition/list
     * 说明: 简单条件通过 URL 参数传入，Spring 会自动将参数绑定到实体对象的对应属性中
     */
    @GetMapping("/list")
    public Result<List<FlowDefinition>> list(FlowDefinition flowDefinition) {
        List<FlowDefinition> list = flowDefinitionService.listFlowDefinitions(flowDefinition);
        return Result.success(list);
    }

    /**
     * 3. 根据 ID 获取流程定义详情
     * 请求方式: GET
     * 路径: /flow/definition/{defId}
     */
    @GetMapping("/{defId}")
    public Result<FlowDefinition> getInfo(@PathVariable("defId") Long defId) {
        if (defId == null) {
            return Result.error("流程ID不能为空");
        }
        FlowDefinition flowDefinition = flowDefinitionService.getFlowDefinitionById(defId);
        if (flowDefinition == null) {
            return Result.error("未找到指定的流程定义");
        }
        return Result.success(flowDefinition);
    }

    /**
     * 4. 修改流程定义模板
     * 请求方式: PUT
     * 路径: /flow/definition
     */
    @PutMapping
    public Result<Void> edit(@RequestBody FlowDefinition flowDefinition) {
        if (flowDefinition.getDefId() == null) {
            return Result.error("待修改流程ID不能为空");
        }

        boolean success = flowDefinitionService.updateFlowDefinition(flowDefinition);
        if (!success) {
            return Result.error("修改流程失败，记录不存在或流程编码已存在");
        }
        return Result.success();
    }

    /**
     * 5. 删除流程定义 (逻辑删除)
     * 请求方式: DELETE
     * 路径: /flow/definition/{defId}
     */
    @DeleteMapping("/{defId}")
    public Result<Void> remove(@PathVariable("defId") Long defId) {
        if (defId == null) {
            return Result.error("待删除流程ID不能为空");
        }

        boolean success = flowDefinitionService.deleteFlowDefinitionById(defId);
        if (!success) {
            return Result.error("删除流程失败，目标记录不存在");
        }
        return Result.success();
    }

    /**
     * 6. 获取可用的流程定义列表 (用于发起申请时的下拉选择)
     * 请求方式: GET
     * 路径: /flow/definition/available
     */
    @GetMapping("/available")
    public Result<List<FlowDefinition>> getAvailableList() {
        // 构建一个状态为 0 (正常启用) 的查询条件
        FlowDefinition query = new FlowDefinition();
        query.setStatus(0);

        // 复用 Service 层已有的条件查询方法
        List<FlowDefinition> list = flowDefinitionService.listFlowDefinitions(query);

        // 哪怕只返回基础实体，Spring 的 Jackson 也会序列化成 JSON
        // 前端取需要的 defId, flowName, flowCode 即可[cite: 7]
        return Result.success(list);
    }
}
