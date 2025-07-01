package io.github.haeun.newsgptback.news.dto;

import io.github.haeun.newsgptback.news.domain.user.User;

public record UserInfoResponse(
        Long id,
        String userId,
        String role
) {
    public UserInfoResponse(User user) {
        this(user.getId(), user.getUserId(), user.getRole().name());
    }
}
