package io.github.haeun.newsgptback.config;

import io.github.haeun.newsgptback.news.service.AuthService;
import io.github.haeun.newsgptback.news.service.BookmarkService;
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

    @Bean
    public AuthService authService() {
        return Mockito.mock(AuthService.class);
    }

    @Bean
    public BookmarkService bookmarkService() {
        return Mockito.mock(BookmarkService.class);
    }

}