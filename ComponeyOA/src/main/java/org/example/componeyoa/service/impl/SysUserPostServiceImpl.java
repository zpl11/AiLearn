package org.example.componeyoa.service.impl;

import org.example.componeyoa.dao.SysUserPostMapper;
import org.example.componeyoa.entity.SysUserPost;
import org.example.componeyoa.service.SysUserPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class SysUserPostServiceImpl implements SysUserPostService {

    @Resource
    private SysUserPostMapper sysUserPostMapper;

    @Override
    public boolean createUserPost(SysUserPost sysUserPost) {
        if (sysUserPost == null || sysUserPost.getUserId() == null || sysUserPost.getPostId() == null) {
            return false;
        }
        return sysUserPostMapper.insertUserPost(sysUserPost) > 0;
    }

    @Override
    public boolean deleteUserPost(SysUserPost sysUserPost) {
        if (sysUserPost == null || sysUserPost.getUserId() == null || sysUserPost.getPostId() == null) {
            return false;
        }
        return sysUserPostMapper.deleteUserPost(sysUserPost) > 0;
    }

    /**
     * 事务保障下的“修改”操作：先物理清空旧关系，再批量插入新关系
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserPosts(Long userId, List<Long> postIds) {
        if (userId == null) {
            return false;
        }

        // 1. 清空当前员工现存的所有岗位关联
        sysUserPostMapper.deleteByUserId(userId);

        // 2. 如果前端勾选了新岗位，重新逐条写入
        if (postIds != null && !postIds.isEmpty()) {
            for (Long postId : postIds) {
                if (postId != null) {
                    SysUserPost userPost = new SysUserPost(userId, postId);
                    sysUserPostMapper.insertUserPost(userPost);
                }
            }
        }

        return true;
    }

    @Override
    public List<SysUserPost> queryAllSysUserPost() {
        return sysUserPostMapper.queryAllUserPost();
    }

    @Override
    public List<SysUserPost> queryAllSysUserByPost(Long postId) {
        if (postId == null) {
            return new ArrayList<>();
        }
        return sysUserPostMapper.queryUsersByPostId(postId);
    }

    @Override
    public List<SysUserPost> queryAllPostByUser(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        return sysUserPostMapper.queryPostsByUserId(userId);
    }

    @Override
    public List<Long> getPostIdsByUserId(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        return sysUserPostMapper.selectPostIdsByUserId(userId);
    }
}