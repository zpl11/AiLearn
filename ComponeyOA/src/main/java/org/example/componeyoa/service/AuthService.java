package org.example.componeyoa.service;

import org.example.componeyoa.entity.dto.LoginDTO;
import org.example.componeyoa.entity.vo.LoginVO;

public interface AuthService {
    /**
     * 用户登录认证
     *
     * @param loginDTO 登录参数（账号、密码）
     * @return 包含 Token 和权限集合的登录视图对象
     */
    LoginVO login(LoginDTO loginDTO);
}
