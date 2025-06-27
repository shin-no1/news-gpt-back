package io.github.haeun.newsgptback.news.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.haeun.newsgptback.config.TestMockConfig;
import io.github.haeun.newsgptback.news.dto.EmailCodeVerifyRequest;
import io.github.haeun.newsgptback.news.dto.EmailRequest;
import io.github.haeun.newsgptback.news.dto.SignupRequest;
import io.github.haeun.newsgptback.news.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({TestMockConfig.class})
@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
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
        SignupRequest request = new SignupRequest("test@example.com", "password", "nickname");
        doNothing().when(authService).signup(any(), any());

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andDo(document("auth-signup",
                        requestFields(
                                fieldWithPath("email").description("사용자 이메일"),
                                fieldWithPath("password").description("사용자 비밀번호"),
                                fieldWithPath("nickname").description("사용자 닉네임")
                        )));
    }
}

