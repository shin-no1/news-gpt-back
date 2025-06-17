package io.github.haeun.newsgptback.service;

import io.github.haeun.newsgptback.dto.NewsResponseDto;
import io.github.haeun.newsgptback.parser.JsoupNewsParser;
import org.springframework.stereotype.Service;

@Service
public class NewsService {
    private final JsoupNewsParser parser;

    public NewsService(JsoupNewsParser parser) {
        this.parser = parser;
    }

    public NewsResponseDto analyze(String url) {
        return parser.parse(url);
    }
}
