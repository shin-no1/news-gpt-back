package io.github.haeun.newsgptback.news.controller;

import io.github.haeun.newsgptback.news.domain.user.User;
import io.github.haeun.newsgptback.news.dto.*;
import io.github.haeun.newsgptback.news.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/send-code")
    public ResponseEntity<Void> sendCode(@RequestBody EmailRequest emailRequest, HttpServletRequest request) {
        authService.sendEmailCode(emailRequest.getEmail(), request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-code")
    public ResponseEntity<Void> verifyCode(@RequestBody EmailCodeVerifyRequest verifyRequest, HttpServletRequest request) {
        authService.verifyEmailCode(verifyRequest, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignupRequest signupRequest, HttpServletRequest request) {
        authService.signup(signupRequest, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@AuthenticationPrincipal User user,
                                         @RequestHeader("X-Device-Id") String deviceId) {
        authService.logout(user, deviceId);
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    @PostMapping("/reissue")
    public ResponseEntity<LoginResponse> reissue(@RequestHeader("Authorization") String bearerToken,
                                                 @RequestHeader("X-Device-Id") String deviceId) {
        String refreshToken = bearerToken.replace("Bearer ", "");
        LoginResponse response = authService.reissue(refreshToken, deviceId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }
}
