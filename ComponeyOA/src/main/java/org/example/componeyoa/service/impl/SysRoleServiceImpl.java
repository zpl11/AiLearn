package org.example.componeyoa.service.impl;

import org.example.componeyoa.dao.SysRoleMapper;
import org.example.componeyoa.entity.SysRole;
import org.example.componeyoa.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class SysRoleServiceImpl implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRole(SysRole sysRole) {
        if (sysRole == null) {
            return false;
        }
        if (sysRole.getStatus() == null) {
            sysRole.setStatus(0);
        }
        return sysRoleMapper.insertRole(sysRole) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(Long roleId) {
        if (roleId == null || roleId <= 0) {
            return false;
        }
        // 1. 校验是否绑定在职员工
        if (sysRoleMapper.countUserRoleByRoleId(roleId) > 0) {
            throw new IllegalStateException("该角色已分配给在职员工，禁止直接删除！");
        }
        // 2. 清理关联菜单权限
        sysRoleMapper.deleteRoleMenusByRoleId(roleId);
        // 3. 删除角色本体
        return sysRoleMapper.deleteRoleById(roleId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRoles(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return false;
        }
        // 逐个校验并删除，确保触发员工绑定校验和关联清理
        for (Long roleId : roleIds) {
            deleteRole(roleId);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(SysRole sysRole) {
        if (sysRole == null || sysRole.getRoleId() == null) {
            return false;
        }
        return sysRoleMapper.updateRole(sysRole) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRoleStatus(Long roleId, Integer status) {
        if (roleId == null || status == null) {
            return false;
        }
        SysRole role = new SysRole();
        role.setRoleId(roleId);
        role.setStatus(status);
        return sysRoleMapper.updateRole(role) > 0;
    }

    @Override
    public SysRole getRoleById(Long roleId) {
        if (roleId == null || roleId <= 0) {
            return null;
        }
        return sysRoleMapper.selectRoleById(roleId);
    }

    @Override
    public List<SysRole> listRoles(String roleName, Integer status) {
        // 如果有搜索条件走条件搜索，否则查全量
        if ((roleName != null && !roleName.trim().isEmpty()) || status != null) {
            String name = (roleName != null && !roleName.trim().isEmpty()) ? roleName.trim() : null;
            List<SysRole> list = sysRoleMapper.selectRolesByName(name);
            return list != null ? list : Collections.emptyList();
        }
        List<SysRole> list = sysRoleMapper.selectAllRoles();
        return list != null ? list : Collections.emptyList();
    }

    @Override
    public List<Long> listMenuIdsByRoleId(Long roleId) {
        if (roleId == null || roleId <= 0) {
            return Collections.emptyList();
        }
        List<Long> menuIds = sysRoleMapper.selectMenuIdsByRoleId(roleId);
        return menuIds != null ? menuIds : Collections.emptyList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignMenus(Long roleId, List<Long> menuIds) {
        if (roleId == null || roleId <= 0) {
            throw new IllegalArgumentException("角色 ID 非法");
        }
        // 1. 清除旧权限映射
        sysRoleMapper.deleteRoleMenusByRoleId(roleId);
        // 2. 批量插入新权限映射
        if (menuIds != null && !menuIds.isEmpty()) {
            sysRoleMapper.batchInsertRoleMenus(roleId, menuIds);
        }
        return true;
    }
}