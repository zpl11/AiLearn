package org.example.componeyoa.controller;

import org.example.componeyoa.common.Result;
import org.example.componeyoa.entity.FlowDefinition;
import org.example.componeyoa.entity.FlowInstance;
import org.example.componeyoa.entity.dto.FlowInstanceDTO;
import org.example.componeyoa.entity.vo.FlowInstanceVO;
import org.example.componeyoa.service.FlowDefinitionService;
import org.example.componeyoa.service.FlowInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/flow/instance")
@CrossOrigin
public class FlowInstanceController {

    @Autowired
    private FlowInstanceService flowInstanceService;

    @Autowired
    private FlowDefinitionService flowDefinitionService;

    /**
     * 后台多条件查询流程实例列表
     */
    @GetMapping("/list")
    public Result<List<FlowInstanceVO>> list(@RequestParam(required = false) String flowCode,
                                             @RequestParam(required = false) Integer status,
                                             @RequestParam(required = false) Long initiatorId) {
        List<FlowInstance> entityList = flowInstanceService.getList(flowCode, status, initiatorId);
        List<FlowInstanceVO> voList = convertEntityToVOList(entityList);
        return Result.success(voList);
    }

    /**
     * 查询【我发起的】审批列表
     */
    @GetMapping("/myInitiate")
    public Result<List<FlowInstanceVO>> myInitiate() {
        // 模拟从上下文获取当前登录用户 ID (以后替换为 Token 解析)
        Long currentUserId = 1L;

        List<FlowInstance> entityList = flowInstanceService.getMyInitiateList(currentUserId);
        List<FlowInstanceVO> voList = convertEntityToVOList(entityList);
        return Result.success(voList);
    }

    /**
     * 根据实例id查询单条详情
     */
    @GetMapping("/{instanceId}")
    public Result<FlowInstanceVO> getInfo(@PathVariable("instanceId") Long instanceId) {
        FlowInstance entity = flowInstanceService.getById(instanceId);
        FlowInstanceVO vo = convertEntityToVO(entity);
        return Result.success(vo);
    }

    /**
     * 发起审批（新增流程实例，并自动派发第一步待办任务）
     */
    @PostMapping("/start")
    public Result<Void> add(@RequestBody FlowInstanceDTO flowInstanceDTO) {
        try {
            if (flowInstanceDTO.getDefId() == null) {
                return Result.error("流程模板ID不能为空");
            }

            // 1. 通过 defId 反查模板，拿到真正的 flowCode
            FlowDefinition definition = flowDefinitionService.getFlowDefinitionById(flowInstanceDTO.getDefId());
            if (definition == null) {
                return Result.error("流程模板不存在");
            }

            // 2. DTO 转 Entity
            FlowInstance flowInstance = new FlowInstance();
            flowInstance.setDefId(flowInstanceDTO.getDefId());
            flowInstance.setFlowCode(definition.getFlowCode());
            flowInstance.setTitle(flowInstanceDTO.getTitle());
            flowInstance.setFormData(flowInstanceDTO.getFormData());

            // 3. 模拟当前登录用户发起（后续接入 Token 解析）
            flowInstance.setInitiatorId(1L);
            flowInstance.setDeptId(1L);

            flowInstance.setCurrentOrder(1);
            flowInstance.setStatus(0);
            flowInstance.setDelFlag(0);

            // 4. 调用 Service 层：不仅插入实例，内部还需完成【查配置 -> 找岗位用户 -> 写入 flow_task 待办】的闭环引擎逻辑
            flowInstanceService.addFlowInstance(flowInstance);

            return Result.success();
        } catch (RuntimeException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    /**
     * 更新流程实例
     */
    @PutMapping
    public Result<Void> edit(@RequestBody FlowInstance flowInstance) {
        try {
            flowInstanceService.updateFlowInstance(flowInstance);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 逻辑删除流程实例
     */
    @DeleteMapping("/{instanceId}")
    public Result<Void> remove(@PathVariable("instanceId") Long instanceId) {
        try {
            flowInstanceService.removeLogicById(instanceId);
            return Result.success();
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // ---------------- 内部转换工具方法：Entity → VO ----------------
    private FlowInstanceVO convertEntityToVO(FlowInstance entity) {
        if (entity == null) {
            return null;
        }
        FlowInstanceVO vo = new FlowInstanceVO();
        vo.setInstanceId(entity.getInstanceId());
        vo.setDefId(entity.getDefId());
        vo.setFlowCode(entity.getFlowCode());
        vo.setTitle(entity.getTitle());
        vo.setInitiatorId(entity.getInitiatorId());
        vo.setDeptId(entity.getDeptId());
        vo.setFormData(entity.getFormData());
        vo.setCurrentOrder(entity.getCurrentOrder());
        vo.setStatus(entity.getStatus());
        vo.setDelFlag(entity.getDelFlag());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }

    private List<FlowInstanceVO> convertEntityToVOList(List<FlowInstance> entityList) {
        List<FlowInstanceVO> voList = new ArrayList<>();
        for (FlowInstance entity : entityList) {
            voList.add(convertEntityToVO(entity));
        }
        return voList;
    }
}