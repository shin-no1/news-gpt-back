package io.github.haeun.newsgptback.news.controller;

import io.github.haeun.newsgptback.common.enums.ErrorCode;
import io.github.haeun.newsgptback.common.exception.CustomException;
import io.github.haeun.newsgptback.news.domain.user.User;
import io.github.haeun.newsgptback.news.dto.UserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getMyInfo(@AuthenticationPrincipal User user) {
        if (user == null) {
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new UserInfoResponse(user));
    }

}
