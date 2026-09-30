package org.example.componeyoa.entity.dto;

import java.io.Serializable;
import java.util.List;

public class UserRoleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 目标用户ID (必须指定)
     */
    private Long userId;

    /**
     * 分配的角色ID列表 (传空集合表示清空该用户的所有角色)
     */
    private List<Long> roleIds;

    public UserRoleDTO() {
    }

    public UserRoleDTO(Long userId, List<Long> roleIds) {
        this.userId = userId;
        this.roleIds = roleIds;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }

    @Override
    public String toString() {
        return "UserRoleDTO{" +
                "userId=" + userId +
                ", roleIds=" + roleIds +
                '}';
    }
}