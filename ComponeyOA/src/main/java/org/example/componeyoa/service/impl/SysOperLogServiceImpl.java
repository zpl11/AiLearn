package org.example.componeyoa.service.impl;

import org.example.componeyoa.common.PageResult;
import org.example.componeyoa.dao.SysOperLogMapper;
import org.example.componeyoa.entity.SysOperLog;
import org.example.componeyoa.entity.dto.SysOperLogQueryDTO;
import org.example.componeyoa.service.SysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SysOperLogServiceImpl implements SysOperLogService {

    @Autowired
    private SysOperLogMapper operLogMapper;

    @Override
    public boolean insertOperLog(SysOperLog operLog) {
        if (operLog == null) {
            return false;
        }
        return operLogMapper.insertOperLog(operLog) > 0;
    }

    @Override
    public PageResult<SysOperLog> queryOperLogPage(SysOperLogQueryDTO queryDTO) {
        if (queryDTO == null) {
            queryDTO = new SysOperLogQueryDTO();
        }
        long total = operLogMapper.countOperLogList(queryDTO);
        if (total == 0) {
            return PageResult.of(Collections.emptyList(), 0, queryDTO.getPageNum(), queryDTO.getPageSize());
        }
        List<SysOperLog> list = operLogMapper.selectOperLogList(queryDTO);
        return PageResult.of(list, total, queryDTO.getPageNum(), queryDTO.getPageSize());
    }

    @Override
    public SysOperLog queryOperLogById(Long operId) {
        if (operId == null) {
            return null;
        }
        return operLogMapper.selectOperLogById(operId);
    }

    @Override
    public boolean deleteOperLogByIds(List<Long> operIds) {
        if (operIds == null || operIds.isEmpty()) {
            return false;
        }
        return operLogMapper.deleteOperLogByIds(operIds) > 0;
    }

    @Override
    public void cleanOperLog() {
        operLogMapper.cleanOperLog();
    }
}
