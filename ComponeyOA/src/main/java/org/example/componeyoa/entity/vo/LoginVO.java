package org.example.componeyoa.entity.vo;

import java.io.Serializable;
import java.util.List;

public class LoginVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String token;
    private Long userId;
    private String userName;
    private String nickName;
    private List<String> permissions; // 权限字符列表，如 ["system:user:add", "system:role:edit"]
    private List<String> roles;       // 角色标识列表，如 ["admin", "hr"]

    public LoginVO() {
    }

    public LoginVO(String token, Long userId, String userName, String nickName, List<String> permissions, List<String> roles) {
        this.token = token;
        this.userId = userId;
        this.userName = userName;
        this.nickName = nickName;
        this.permissions = permissions;
        this.roles = roles;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "LoginVO{" +
                "token='" + token + '\'' +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", nickName='" + nickName + '\'' +
                ", permissions=" + permissions +
                ", roles=" + roles +
                '}';
    }
}