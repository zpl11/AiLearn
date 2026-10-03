package org.example.componeyoa.controller;

import org.example.componeyoa.common.Result;
import org.example.componeyoa.entity.FlowNodeConfig;
import org.example.componeyoa.entity.vo.FlowNodeConfigVO;
import org.example.componeyoa.service.IFlowNodeConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 流程节点配置 控制器
 */
@RestController
@RequestMapping("/flow/node")
@CrossOrigin
public class FlowNodeConfigController {

    @Autowired
    private IFlowNodeConfigService flowNodeConfigService;

    /**
     * 根据流程定义ID获取所有配置节点（按 node_order 升序并带 postName）
     */
    @GetMapping("/list/{defId}")
    public Result<List<FlowNodeConfigVO>> listByDefId(@PathVariable("defId") Long defId) {
        return Result.success(flowNodeConfigService.listByDefId(defId));
    }

    /**
     * 获取节点详细信息（用于编辑回显）
     */
    @GetMapping("/{nodeId}")
    public Result<FlowNodeConfig> getInfo(@PathVariable("nodeId") Long nodeId) {
        FlowNodeConfig nodeConfig = flowNodeConfigService.getById(nodeId);
        if (nodeConfig == null) {
            return Result.error("未找到对应的流程节点信息");
        }
        return Result.success(nodeConfig);
    }

    /**
     * 新增审批节点
     */
    @PostMapping
    public Result<Void> add(@RequestBody FlowNodeConfig nodeConfig) {
        try {
            boolean success = flowNodeConfigService.addNode(nodeConfig);
            return success ? Result.success() : Result.error("新增审批节点失败");
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改审批节点
     */
    @PutMapping
    public Result<Void> edit(@RequestBody FlowNodeConfig nodeConfig) {
        try {
            boolean success = flowNodeConfigService.updateNode(nodeConfig);
            return success ? Result.success() : Result.error("修改审批节点失败");
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除审批节点
     */
    @DeleteMapping("/{nodeId}")
    public Result<Void> remove(@PathVariable("nodeId") Long nodeId) {
        boolean success = flowNodeConfigService.deleteNode(nodeId);
        return success ? Result.success() : Result.error("删除审批节点失败");
    }
}
