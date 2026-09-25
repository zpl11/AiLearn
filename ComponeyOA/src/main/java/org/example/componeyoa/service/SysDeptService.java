package org.example.componeyoa.service;


import org.example.componeyoa.entity.SysDept;

import java.util.List;

/**
 * Controller 层面接收请求
 * Service 层面进行登录校验，数据组装，事务控制都是在 Service 中进行的。
 *
 * */
public interface SysDeptService {

//    查询所有部门列表
    List<SysDept> queryDeptList();

    // 根据 ID 查询部门
    SysDept queryDeptById(Long deptId);

    // 新增部门
    boolean addDept(SysDept dept);

    // 修改部门
    boolean updateDept(SysDept dept);

    // 删除部门
    boolean deleteDeptById(Long deptId);

}