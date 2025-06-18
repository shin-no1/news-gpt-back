package io.github.haeun.newsgptback.service;

import io.github.haeun.newsgptback.dto.GptResponse;
import io.github.haeun.newsgptback.dto.NewsResponse;
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

    public NewsResponse summarizeUrl(String url) {
        NewsResponse newsResponse = parser.parse(url);
        GptResponse gptResponse = gptClient.summarize(newsResponse.getSummary());
        return new NewsResponse(newsResponse.getTitle(), gptResponse.getSummary(), gptResponse.getTopic(), gptResponse.getKeywords(), url);
    }
}
