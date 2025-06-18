package io.github.haeun.newsgptback.service;

import io.github.haeun.newsgptback.dto.GptResponseDto;
import io.github.haeun.newsgptback.dto.NewsResponseDto;
import io.github.haeun.newsgptback.gpt.GptClient;
import io.github.haeun.newsgptback.parser.JsoupNewsParser;
import org.springframework.stereotype.Service;

@Service
public class NewsService {
    private final JsoupNewsParser parser;
    private final GptClient gptClient;

    public NewsService(JsoupNewsParser parser, GptClient gptClient) {
        this.parser = parser;
        this.gptClient = gptClient;
    }

    public NewsResponseDto summarizeUrl(String url) {
        NewsResponseDto newsResponseDto = parser.parse(url);
        GptResponseDto gptResponseDto = gptClient.summarize(newsResponseDto.getSummary());
        return new NewsResponseDto(newsResponseDto.getTitle(), gptResponseDto.getSummary(), gptResponseDto.getTopic(), gptResponseDto.getKeywords(), url);
    }
}
