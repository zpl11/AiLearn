package org.example.componeyoa.service.impl;

import org.example.componeyoa.common.JwtUtils;
import org.example.componeyoa.dao.SysMenuMapper;
import org.example.componeyoa.dao.SysRoleMapper;
import org.example.componeyoa.dao.SysUserMapper;
import org.example.componeyoa.entity.SysUser;
import org.example.componeyoa.entity.dto.LoginDTO;
import org.example.componeyoa.entity.vo.LoginVO;
import org.example.componeyoa.service.AuthService;
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
        // 1. 基础防御校验
        if (loginDTO == null || loginDTO.getUserName() == null || loginDTO.getPassword() == null) {
            throw new IllegalArgumentException("用户名或密码不能为空");
        }
        String userName = loginDTO.getUserName().trim();
        String password = loginDTO.getPassword().trim();

        // 2. 根据用户名查询用户是否存在
        SysUser user = sysUserMapper.selectByUserName(userName);
        if (user == null) {
            // 安全防线：不要明确提示“用户不存在”，统称“用户名或密码错误”，防止黑客枚举用户名
            throw new RuntimeException("用户名或密码错误");
        }

        // 3. 校验账号状态（0: 正常, 1: 停用）
        if (user.getStatus() != null && user.getStatus() == 1) {
            throw new RuntimeException("该账号已被停用，请联系管理员");
        }

        // 4. 密码校验
        // 提示：如果你数据库里目前还是明文，先用 equals 比对；
        // 如果使用了 BCrypt，此处换成 passwordEncoder.matches(password, user.getPassword())
        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 5. 权限聚合：查出该用户所有有效角色编码与菜单权限标识
        List<String> roles = sysRoleMapper.selectRoleKeysByUserId(user.getUserId());
        List<String> permissions = sysMenuMapper.selectPermsByUserId(user.getUserId());

        // 6. 签发 JWT Token
        String token = JwtUtils.createToken(user.getUserId(), user.getUserName());

        // 7. 组装并返回 LoginVO
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
