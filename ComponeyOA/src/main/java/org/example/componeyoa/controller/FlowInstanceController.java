package org.example.componeyoa.controller;

import org.example.componeyoa.common.Result;
import org.example.componeyoa.entity.FlowInstance;
import org.example.componeyoa.entity.dto.FlowInstanceDTO;
import org.example.componeyoa.entity.vo.FlowInstanceVO;
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

    /**
     * 后台多条件查询流程实例列表
     */
    @GetMapping("/list")
    public Result<List<FlowInstanceVO>> list(@RequestParam(required = false) String flowCode,
                                             @RequestParam(required = false) Integer status,
                                             @RequestParam(required = false) Long initiatorId) {
        List<FlowInstance> entityList = flowInstanceService.getList(flowCode, status, initiatorId);
        // 简单把entity转vo，实际项目建议抽转换工具方法
        List<FlowInstanceVO> voList = convertEntityToVOList(entityList);
        return Result.success(voList);
    }

    /**
     * 查询【我发起的】审批列表
     */
    @GetMapping("/myInitiate")
    public Result<List<FlowInstanceVO>> myInitiate(@RequestParam Long initiatorId) {
        List<FlowInstance> entityList = flowInstanceService.getMyInitiateList(initiatorId);
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
     * 发起审批（新增流程实例）
     * 修改了这里的路径，使其匹配前端的 /flow/instance/start
     */
    @PostMapping("/start") // <-- 就是改这里
    public Result<Void> add(@RequestBody FlowInstanceDTO flowInstanceDTO) {
        try {
            // DTO 转 Entity
            FlowInstance flowInstance = new FlowInstance();
            flowInstance.setDefId(flowInstanceDTO.getDefId());
            flowInstance.setFlowCode(flowInstanceDTO.getFlowCode());
            flowInstance.setTitle(flowInstanceDTO.getTitle());
            flowInstance.setFormData(flowInstanceDTO.getFormData());

            // ========= 注意：下面这几个字段业务层从登录上下文获取！=========
            // flowInstance.setInitiatorId(登录用户ID);
            // flowInstance.setDeptId(登录用户部门ID);
            flowInstance.setCurrentOrder(1);
            flowInstance.setStatus(0);
            flowInstance.setDelFlag(0);

            flowInstanceService.addFlowInstance(flowInstance);
            return Result.success();
        } catch (RuntimeException e) {
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
    /**
     * 单个对象转换
     */
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
        // 扩展字段 initiatorNickName、flowName 需要联表查询后在这里赋值
        return vo;
    }

    /**
     * 集合转换
     */
    private List<FlowInstanceVO> convertEntityToVOList(List<FlowInstance> entityList) {
        List<FlowInstanceVO> voList = new ArrayList<>();
        for (FlowInstance entity : entityList) {
            voList.add(convertEntityToVO(entity));
        }
        return voList;
    }

}
