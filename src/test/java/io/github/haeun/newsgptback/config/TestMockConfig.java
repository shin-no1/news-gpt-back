package io.github.haeun.newsgptback.config;

import io.github.haeun.newsgptback.news.service.NewsService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestMockConfig {
    @Bean
    public NewsService newsService() {
        return Mockito.mock(NewsService.class);
    }
}