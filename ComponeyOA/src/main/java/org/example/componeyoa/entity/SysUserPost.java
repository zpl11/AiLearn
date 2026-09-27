package org.example.componeyoa.entity;

public class SysUserPost {
    private Long userId;
    private Long postId;

    public SysUserPost(){};

    // 2. 全参构造（留给自己在业务代码里方便 new）
    public SysUserPost(Long userId, Long postId) {
        this.userId = userId;
        this.postId = postId;
    }

    public Long getUserId() {
        return userId;
    }



    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    @Override
    public String toString() {
        return "SysUserPost{" +
                "userId=" + userId +
                ", postId=" + postId +
                '}';
    }
}
