package org.example.componeyoa.service.impl;

import org.example.componeyoa.dao.SysDeptMapper;
import org.example.componeyoa.entity.SysDept;
import org.example.componeyoa.service.SysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysDeptServiceImpl implements SysDeptService {

    @Autowired
    private SysDeptMapper deptMapper;

    @Override
    public List<SysDept> queryDeptList() {
        return deptMapper.selectAll();
    }

    @Override
    public SysDept queryDeptById(Long deptId) {
        return deptMapper.selectById(deptId);
    }

    @Override
    public boolean addDept(SysDept dept) {
        // 如果是顶级部门(parentId = 0),祖级列表直接设为"0"
        if(dept.getParentId() == null || dept.getParentId() == 0L) {
            dept.setParentId(0L);
            dept.setParentIds("0");
        }else {
            SysDept parentDept = deptMapper.selectById(dept.getParentId());
            if(parentDept == null) {
                throw new RuntimeException("所选父级部门不存在!");
            }
            dept.setParentIds(parentDept.getParentIds()+","+parentDept.getDeptId());
        }
        return deptMapper.insertDept(dept) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDept(SysDept dept) {
        // 防御：修改操作必须带上合法的 deptId
        if (dept.getDeptId() == null || dept.getDeptId() <= 0) {
            throw new RuntimeException("修改失败：部门ID不能为空！");
        }

        if(dept.getDeptId().equals(dept.getParentId())) {
            throw new RuntimeException("修改失败：不能将自身设置为上级部门");
        }
        if(dept.getParentId() == 0L) {
            dept.setParentIds("0");
        }else {
            SysDept parentDept = deptMapper.selectById(dept.getParentId());
            if(parentDept == null){
                throw new RuntimeException("所选父级部门不存在");
            }
            dept.setParentIds(parentDept.getParentIds()+","+parentDept.getDeptId());
        }
        return deptMapper.updateDept(dept) > 0;
    }

    @Override
    public boolean deleteDeptById(Long deptId) {
        int childCount = deptMapper.countChildrenByParentId(deptId);
        if(childCount > 0 ) {
            throw new RuntimeException("删除失败: 该部门下仍然存在子部门，不允许删除");
        }
        return deptMapper.deleteDeptByID(deptId) > 0;
    }
}
