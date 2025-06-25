package io.github.haeun.newsgptback.news.controller;

import io.github.haeun.newsgptback.common.enums.ErrorCode;
import io.github.haeun.newsgptback.common.exception.CustomException;
import io.github.haeun.newsgptback.common.util.RateLimiter;
import io.github.haeun.newsgptback.news.dto.NewsRequest;
import io.github.haeun.newsgptback.news.dto.NewsResponse;
import io.github.haeun.newsgptback.news.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
@Tag(name = "뉴스 요약", description = "뉴스 기사 URL을 기반으로 GPT 요약을 수행하는 API")
public class NewsController {
    private final NewsService newsService;
    private final RateLimiter rateLimiter;

    /**
     * 뉴스 기사 URL을 받아 GPT 기반 요약 결과를 반환하는 API
     *
     * @param newsRequest 요약할 뉴스 기사 URL
     * @return 분석된 뉴스 결과 (제목, 요약, 주제, 키워드)
     */
    @Operation(
            summary = "뉴스 요약 분석",
            description = "뉴스 기사 URL을 입력받아 제목, 요약, 키워드 등을 반환합니다."
    )
    @Parameters({@Parameter(name = "url", description = "URL", example = "https://n.news.naver.com/article/015/0005146248")})
    @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = NewsResponse.class)))
    @PostMapping("/analyze-url")
    public ResponseEntity<?> analyzeUrl(@RequestBody NewsRequest newsRequest, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String key = "rate:" + ip + ":" + LocalDate.now();

        int ANALYZE_LIMIT = 5, ANALYZE_TTL_SECONDS = 86400;
        boolean allowed = rateLimiter.isAllowed(key, ANALYZE_LIMIT, ANALYZE_TTL_SECONDS);
        if (!allowed) {
            throw new CustomException(ErrorCode.RATE_LIMIT_EXCEEDED);
        }

        NewsResponse newsResponse = newsService.getNewsResponse(newsRequest.getUrl());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(newsResponse);
    }
}