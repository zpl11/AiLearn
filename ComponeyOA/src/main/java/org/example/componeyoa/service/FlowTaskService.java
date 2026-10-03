package org.example.componeyoa.service;

import org.example.componeyoa.entity.vo.FlowTaskVO;

import java.util.List;

public interface FlowTaskService {

    // 严格遵循你的总结：在接口中免除 public，并且使用基本类型 boolean 返回，避免 Boolean 的 Null 第三态问题引发 Bug

    /**
     * 获取我的待办任务列表
     * @param userId 审批人ID
     * @return 待办任务视图列表
     */
    List<FlowTaskVO> getMyTodoList(Long userId);

    /**
     * 获取我的已办任务列表
     * @param userId 审批人ID
     * @param status 任务状态 (1已同意 2已驳回)
     * @return 已办任务视图列表
     */
    List<FlowTaskVO> getMyDoneList(Long userId, Integer status);

    /**
     * 审批处理 (同意/驳回)
     * @param taskId 任务ID
     * @param status 目标状态 (1同意, 2驳回)
     * @param comment 审批意见
     * @return 是否处理成功
     */
    boolean processTask(Long taskId, Integer status, String comment);
}
