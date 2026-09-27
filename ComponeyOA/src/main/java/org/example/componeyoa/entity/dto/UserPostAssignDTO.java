package org.example.componeyoa.entity.dto;

import java.util.List;

public class UserPostAssignDTO {
    private Long userId;
    private List<Long> postIds;

    public UserPostAssignDTO() {}

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Long> getPostIds() {
        return postIds;
    }

    public void setPostIds(List<Long> postIds) {
        this.postIds = postIds;
    }
}
