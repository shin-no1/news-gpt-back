package io.github.haeun.newsgptback.news.controller;

import io.github.haeun.newsgptback.common.enums.ErrorCode;
import io.github.haeun.newsgptback.common.enums.UserRole;
import io.github.haeun.newsgptback.common.exception.CustomException;
import io.github.haeun.newsgptback.common.util.RateLimiter;
import io.github.haeun.newsgptback.news.domain.user.User;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api/news")
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
    public ResponseEntity<?> analyzeUrl(@AuthenticationPrincipal User user, @RequestBody NewsRequest newsRequest, HttpServletRequest request) {
        int ANALYZE_LIMIT = ObjectUtils.isEmpty(user) ? 5 : user.getRole() == UserRole.ADMIN ? 100 : 10;
        int ANALYZE_TTL_SECONDS = 86400;
        String ip = request.getRemoteAddr();
        String key = "rate:" + ip + ":" + LocalDate.now();
        long current = rateLimiter.getCurrentCount(key);
        if (current >= ANALYZE_LIMIT) {
            throw new CustomException(ErrorCode.RATE_LIMIT_EXCEEDED);
        }
        NewsResponse newsResponse = newsService.getNewsResponse(newsRequest.getUrl(), ip);
        rateLimiter.incrementWithTtlIfNeeded(key, ANALYZE_TTL_SECONDS);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(newsResponse);
    }
}