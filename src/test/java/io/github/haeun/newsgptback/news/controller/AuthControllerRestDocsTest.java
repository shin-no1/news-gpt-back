package io.github.haeun.newsgptback.news.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.haeun.newsgptback.common.enums.UserRole;
import io.github.haeun.newsgptback.config.TestMockConfig;
import io.github.haeun.newsgptback.news.dto.*;
import io.github.haeun.newsgptback.news.service.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({TestMockConfig.class})
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthService authService;

    @Test
    void sendCode() throws Exception {
        EmailRequest request = new EmailRequest("test@example.com");
        doNothing().when(authService).sendEmailCode(any(), any());

        mockMvc.perform(post("/api/auth/send-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("auth-send-code",
                        requestFields(
                                fieldWithPath("email").description("사용자 이메일")
                        )));
    }

    @Test
    void verifyCode() throws Exception {
        EmailCodeVerifyRequest request = new EmailCodeVerifyRequest("test@example.com", "123456");
        doNothing().when(authService).verifyEmailCode(any(), any());

        mockMvc.perform(post("/api/auth/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("auth-verify-code",
                        requestFields(
                                fieldWithPath("email").description("사용자 이메일"),
                                fieldWithPath("code").description("이메일 인증 코드")
                        )));
    }

    @Test
    void signup() throws Exception {
        SignupRequest request = new SignupRequest("test@example.com", "password", "userId");
        doNothing().when(authService).signup(any(), any());

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("auth-signup",
                        requestFields(
                                fieldWithPath("email").description("사용자 이메일"),
                                fieldWithPath("password").description("사용자 비밀번호"),
                                fieldWithPath("username").description("사용자 닉네임")
                        )));
    }

    @Test
    void login() throws Exception {
        LoginRequest request = new LoginRequest("username", "password", "test-device");
        LoginResponse response = new LoginResponse("accessToken", "refreshToken", "username", UserRole.USER.name());
        Mockito.when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("auth-login",
                        requestFields(
                                fieldWithPath("username").description("사용자아이디"),
                                fieldWithPath("password").description("사용자 비밀번호"),
                                fieldWithPath("deviceId").description("디바이스 ID")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").description("accessToken"),
                                fieldWithPath("refreshToken").description("refreshToken"),
                                fieldWithPath("username").description("사용자아이디"),
                                fieldWithPath("role").description("사용자 권한")
                        )
                ));
    }

    @Test
    void logout() throws Exception {
        doNothing().when(authService).logout(any(), any());

        mockMvc.perform(post("/api/auth/logout")
                        .header("X-Device-Id", "device123"))
                .andExpect(status().isOk())
                .andDo(document("auth-logout",
                        requestHeaders(
                                headerWithName("X-Device-Id").description("디바이스 ID(UUID/storage 저장 후 사용)")
                        ),
                        responseFields(
                                fieldWithPath("message").description("성공 메세지")
                        )
                ));
    }

    @Test
    void reissue() throws Exception {
        LoginResponse response = new LoginResponse("newAccessToken", "newRefreshToken", "username", UserRole.USER.name());
        Mockito.when(authService.reissue(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/reissue")
                        .header("Authorization", "Bearer refreshToken")
                        .header("X-Device-Id", "device123"))
                .andExpect(status().isOk())
                .andDo(document("auth-reissue",
                        requestHeaders(
                                headerWithName("Authorization").description("JWT 인증 토큰"),
                                headerWithName("X-Device-Id").description("device123")
                        ),
                        responseFields(
                                fieldWithPath("accessToken").description("accessToken"),
                                fieldWithPath("refreshToken").description("refreshToken"),
                                fieldWithPath("username").description("사용자아이디"),
                                fieldWithPath("role").description("사용자 권한")
                        )
                ));
    }
}

