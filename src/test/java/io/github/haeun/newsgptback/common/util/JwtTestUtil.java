package io.github.haeun.newsgptback.common.util;

import io.github.haeun.newsgptback.common.enums.UserRole;
import io.github.haeun.newsgptback.news.domain.user.User;

public class JwtTestUtil {

    private final JwtUtil jwtUtil;

    public JwtTestUtil(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * 사용자 ID와 기본 정보로 access token 생성
     */
    public String generateAccessToken() {
        User user = new User(1L, "user@email.com", "testuser", "testuser", UserRole.USER, true);
        return jwtUtil.generateAccessToken(user);
    }

    /**
     * 사용자 ID와 기본 정보로 refresh token 생성
     */
    public String generateRefreshToken() {
        User user = new User(1L, "user@email.com", "testuser", "testuser", UserRole.USER, true);
        return jwtUtil.generateRefreshToken(user);
    }
}
