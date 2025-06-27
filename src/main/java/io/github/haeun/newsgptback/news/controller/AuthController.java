package io.github.haeun.newsgptback.news.controller;

import io.github.haeun.newsgptback.news.dto.EmailCodeVerifyRequest;
import io.github.haeun.newsgptback.news.dto.EmailRequest;
import io.github.haeun.newsgptback.news.dto.SignupRequest;
import io.github.haeun.newsgptback.news.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
