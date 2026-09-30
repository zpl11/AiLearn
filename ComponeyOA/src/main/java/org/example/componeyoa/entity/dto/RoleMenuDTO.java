package org.example.componeyoa.entity.dto;

import java.io.Serializable;
import java.util.List;

public class RoleMenuDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 目标角色ID (必须指定)
     */
    private Long roleId;

    /**
     * 勾选的菜单/权限ID列表 (传空集合表示清空该角色的所有权限)
     */
    private List<Long> menuIds;

    public RoleMenuDTO() {
    }

    public RoleMenuDTO(Long roleId, List<Long> menuIds) {
        this.roleId = roleId;
        this.menuIds = menuIds;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public List<Long> getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(List<Long> menuIds) {
        this.menuIds = menuIds;
    }

    @Override
    public String toString() {
        return "RoleMenuDTO{" +
                "roleId=" + roleId +
                ", menuIds=" + menuIds +
                '}';
    }
}