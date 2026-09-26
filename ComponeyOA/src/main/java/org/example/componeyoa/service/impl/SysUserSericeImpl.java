package org.example.componeyoa.service.impl;

import org.example.componeyoa.dao.SysUserMapper;
import org.example.componeyoa.entity.SysUser;
import org.example.componeyoa.service.SysUserService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysUserSericeImpl implements SysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public boolean createSysUser(SysUser sysUser) {
        // 1. 基础非空校验（登录账号、姓名、密码是必填核心凭证）
        if (sysUser == null ||
                sysUser.getUserName() == null || sysUser.getUserName().trim().isEmpty() ||
                sysUser.getNickName() == null || sysUser.getNickName().trim().isEmpty() ||
                sysUser.getPassword() == null || sysUser.getPassword().trim().isEmpty()) {
            return false;
        }

        // 2. 账号唯一性排重（先查后插，保护数据库唯一约束）
        SysUser existUser = sysUserMapper.selectByUserName(sysUser.getUserName());
        if (existUser != null) {
            // 账号已存在，业务拒绝
            return false;
        }

        // 3. 密码加密：自动生成盐值并进行单向哈希，生成长约 60 位的密文字符串
        String encodedPassword = BCrypt.hashpw(sysUser.getPassword(), BCrypt.gensalt());
        sysUser.setPassword(encodedPassword);

        // 4. 执行插入并根据受影响行数返回结果
        return sysUserMapper.addUser(sysUser) > 0;
    }

    @Override
    public boolean delSysUser(Long userId) {
        if (userId == null) {
            return false;
        }
        // 假删除（逻辑删除），受影响行数大于 0 表示成功
        return sysUserMapper.delUser(userId) > 0;
    }

    @Override
    public boolean updateSysUser(SysUser sysUser) {
        // 修改必须指定具体的员工ID
        if (sysUser == null || sysUser.getUserId() == null) {
            return false;
        }
        return sysUserMapper.updateUser(sysUser) > 0;
    }

    @Override
    public List<SysUser> queryAllSysUser() {
        return sysUserMapper.selectAll();
    }

    @Override
    public List<SysUser> queryUserByDeptId(Long deptId) {
        if (deptId == null) {
            return sysUserMapper.selectAll();
        }
        return sysUserMapper.selectByDeptId(deptId);
    }

    @Override
    public SysUser queryUserByUserId(Long userId) {
        if (userId == null) {
            return null;
        }
        SysUser user = sysUserMapper.selectById(userId);
        // 安全起见，单查员工信息时抹除密码哈希，避免回显泄露
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }
}
