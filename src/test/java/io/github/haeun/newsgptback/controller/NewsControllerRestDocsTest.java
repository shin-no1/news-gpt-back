package io.github.haeun.newsgptback.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.haeun.newsgptback.config.TestMockConfig;
import io.github.haeun.newsgptback.news.dto.NewsRequest;
import io.github.haeun.newsgptback.news.dto.NewsResponse;
import io.github.haeun.newsgptback.news.service.NewsService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@AutoConfigureMockMvc
@SpringBootTest
@Import({TestMockConfig.class})
public class NewsControllerRestDocsTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NewsService newsService;

    @Test
    void analyzeUrl() throws Exception {
        // given
        String url = "https://n.news.naver.com/article/015/0005146248";
        NewsRequest request = new NewsRequest(url, false);
        NewsResponse mockResponse = new NewsResponse(
                "뉴스 제목",
                "요약된 본문",
                "사회",
                List.of("키워드1", "키워드2"),
                url
        );

        // when
        Mockito.when(newsService.getNewsResponse(anyString(), anyString(), any())).thenReturn(mockResponse);

        // then: RestDocs 문서화
        mockMvc.perform(post("/api/news/analyze-url")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("analyze-url",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("url").description("뉴스 기사 URL"),
                                fieldWithPath("login").description("ACCESS 토큰 여부")
                        ),
                        responseFields(
                                fieldWithPath("title").description("뉴스 제목"),
                                fieldWithPath("summary").description("요약된 본문"),
                                fieldWithPath("topic").description("기사 주제"),
                                fieldWithPath("keywords").description("핵심 키워드 리스트"),
                                fieldWithPath("url").description("기사 URL")
                        )
                ));
    }

}

