package io.github.haeun.newsgptback.controller;

import io.github.haeun.newsgptback.dto.NewsRequestDto;
import io.github.haeun.newsgptback.dto.NewsResponseDto;
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
    public NewsResponseDto analyzeUrl(@RequestBody NewsRequestDto request) {
        return newsService.analyze(request.getUrl());
    }
}