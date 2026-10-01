package org.example.componeyoa.service.impl;

import org.example.componeyoa.common.JwtUtils;
import org.example.componeyoa.dao.SysMenuMapper;
import org.example.componeyoa.dao.SysRoleMapper;
import org.example.componeyoa.dao.SysUserMapper;
import org.example.componeyoa.entity.SysUser;
import org.example.componeyoa.entity.dto.LoginDTO;
import org.example.componeyoa.entity.vo.LoginVO;
import org.example.componeyoa.service.AuthService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        if (loginDTO == null || loginDTO.getUserName() == null || loginDTO.getPassword() == null) {
            throw new IllegalArgumentException("用户名或密码不能为空");
        }
        String userName = loginDTO.getUserName().trim();
        String password = loginDTO.getPassword().trim();

        // 1. 查询用户
        SysUser user = sysUserMapper.selectByUserName(userName);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 2. 状态检查（0 正常，1 停用）
        if (user.getStatus() != null && user.getStatus() == 1) {
            throw new RuntimeException("该账号已被停用，请联系管理员");
        }

        // 3. 核心修复：使用 BCrypt 校验明文密码与数据库密文哈希
        // BCrypt.checkpw(明文, 数据库中的密文)
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 4. 权限与角色聚合
        List<String> roles = sysRoleMapper.selectRoleKeysByUserId(user.getUserId());
        List<String> permissions = sysMenuMapper.selectPermsByUserId(user.getUserId());

        // 5. 签发 JWT
        String token = JwtUtils.createToken(user.getUserId(), user.getUserName());

        // 6. 封装返回
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserId(user.getUserId());
        loginVO.setUserName(user.getUserName());
        loginVO.setNickName(user.getNickName());
        loginVO.setRoles(roles);
        loginVO.setPermissions(permissions);

        return loginVO;
    }
}
