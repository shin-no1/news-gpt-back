package io.github.haeun.newsgptback.news.dto;

import io.github.haeun.newsgptback.news.domain.user.User;

public record UserInfoResponse(
        Long id,
        String email,
        String nickname,
        String role
) {
    public UserInfoResponse(User user) {
        this(user.getId(), user.getEmail(), user.getUserId(), user.getRole().name());
    }
}
