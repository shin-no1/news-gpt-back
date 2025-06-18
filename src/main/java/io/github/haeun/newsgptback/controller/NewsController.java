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

    @PostMapping("/analyze-url")
    public NewsResponse analyzeUrl(@RequestBody NewsRequest request) {
        NewsResponse newsResponse = newsService.getNewsResponse(request.getUrl());
        if (newsResponse == null) throw new RuntimeException("process error!");
        return newsResponse;
    }
}