package org.example.componeyoa.service;

import org.example.componeyoa.entity.SysPost;

import java.util.List;

public interface SysPostService {

    //新增职位
    boolean addPost(SysPost sysPost);

    // 删除职位
    boolean delPost(Long sysPostId);

    // 修改职位
    boolean updatePost(SysPost sysPost);

    // 查询全部职位
    List<SysPost> queryAllPost();

    // 根据职位名称查询职位
    List<SysPost> queryPostByPostName(String sysPostName);

    // 根据职位id查询
    SysPost queryPostByPostId(Long postId);
}
