package io.github.haeun.newsgptback.controller;


import io.github.haeun.newsgptback.common.enums.UserRole;
import io.github.haeun.newsgptback.common.util.JwtTestUtil;
import io.github.haeun.newsgptback.config.TestJwtConfig;
import io.github.haeun.newsgptback.config.TestMockConfig;
import io.github.haeun.newsgptback.news.domain.user.User;
import io.github.haeun.newsgptback.news.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.headers.RequestHeadersSnippet;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({TestMockConfig.class, TestJwtConfig.class})
@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
@AutoConfigureMockMvc
@AutoConfigureRestDocs
public class UserControllerDocsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private JwtTestUtil jwtTestUtil;

    private String accessToken;

    @BeforeEach
    void setup() {
        User user = new User(1L, "user@email.com", "testuser", "tupw15741", UserRole.USER, true);
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        accessToken = jwtTestUtil.generateAccessToken();
    }

    @Test
    void getMyInfo() throws Exception {
        mockMvc.perform(get("/api/user/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("get-my-info",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        getRequestAuthHeader(),
                        responseFields(
                                fieldWithPath("id").description("사용자 ID"),
                                fieldWithPath("username").description("사용자 아이디"),
                                fieldWithPath("role").description("사용자 권한")
                        )
                ));
    }

    private RequestHeadersSnippet getRequestAuthHeader() {
        return requestHeaders(
                headerWithName("Authorization").description("JWT 인증 토큰")
        );
    }
}
