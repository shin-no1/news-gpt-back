package io.github.haeun.newsgptback.controller;

import io.github.haeun.newsgptback.dto.NewsRequest;
import io.github.haeun.newsgptback.dto.NewsResponse;
import io.github.haeun.newsgptback.service.NewsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class NewsController {
    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    /**
     * 뉴스 기사 URL을 받아 GPT 기반 요약 결과를 반환하는 API
     *
     * @param request 요약할 뉴스 기사 URL
     * @return 분석된 뉴스 결과 (제목, 요약, 주제, 키워드)
     */
    @PostMapping("/analyze-url")
    public NewsResponse analyzeUrl(@RequestBody NewsRequest request) {
        NewsResponse newsResponse = newsService.getNewsResponse(request.getUrl());
        if (newsResponse == null) throw new RuntimeException("process error!");
        return newsResponse;
    }
}