package org.example.componeyoa.service.impl;

import org.example.componeyoa.dao.SysPostMapper;
import org.example.componeyoa.entity.SysPost;
import org.example.componeyoa.service.SysPostService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SysPostServiceImpl implements SysPostService {

    @Resource
    private SysPostMapper sysPostMapper;

    @Override
    public boolean addPost(SysPost sysPost) {
        // 2. 基础非空校验：对象、名称、编码必须同时满足非空
        if (sysPost == null
                || sysPost.getPostName() == null || sysPost.getPostName().trim().isEmpty()
                || sysPost.getPostCode() == null || sysPost.getPostCode().trim().isEmpty()) {
            return false;
        }

        // 3. 业务排重：岗位编码必须具备唯一性契约
        SysPost existPost = sysPostMapper.selectByPostCode(sysPost.getPostCode());
        if (existPost != null) {
            return false; // 编码已存在，拒绝新增
        }

        // 4. 只执行一次落库，通过受影响行数决定成败
        int rows = sysPostMapper.addSysPost(sysPost);
        return rows > 0;
    }

    @Override
    public boolean delPost(Long sysPostId) {
        if (sysPostId == null || sysPostId <= 0) {
            return false;
        }
        // 执行一次逻辑删除
        int rows = sysPostMapper.delSysPost(sysPostId);
        return rows > 0;
    }

    @Override
    public boolean updatePost(SysPost sysPost) {
        // 修改必须校验主键 ID 是否存在
        if (sysPost == null || sysPost.getPostId() == null) {
            return false;
        }
        // 执行一次修改更新
        int rows = sysPostMapper.updateSysPost(sysPost);
        return rows > 0;
    }

    @Override
    public List<SysPost> queryAllPost() {
        return sysPostMapper.queryAllSysPost();
    }

    @Override
    public List<SysPost> queryPostByPostName(String sysPostName) {
        return sysPostMapper.queryByLikeSysPost(sysPostName);
    }

    @Override
    public SysPost queryPostByPostId(Long postId) {
        if (postId == null) {
            return null;
        }
        return sysPostMapper.queryByIdSysPost(postId);
    }
}
