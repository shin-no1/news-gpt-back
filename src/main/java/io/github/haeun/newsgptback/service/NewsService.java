package io.github.haeun.newsgptback.service;

import io.github.haeun.newsgptback.dto.GptResponse;
import io.github.haeun.newsgptback.dto.NewsResponse;
import io.github.haeun.newsgptback.gpt.GptClient;
import io.github.haeun.newsgptback.parser.JsoupNewsParser;
import org.springframework.stereotype.Service;

@Service
public class NewsService {
    private final JsoupNewsParser jsoupNewsParser;
    private final GptClient gptClient;

    public NewsService(JsoupNewsParser jsoupNewsParser, GptClient gptClient) {
        this.jsoupNewsParser = jsoupNewsParser;
        this.gptClient = gptClient;
    }

    public NewsResponse getNewsResponse(String url) {
        NewsResponse newsResponse = jsoupNewsParser.parse(url);
        if (newsResponse == null) return null;

        GptResponse gptResponse = gptClient.summarize(newsResponse.getSummary());
        if (gptResponse == null) return null;

        return new NewsResponse(newsResponse.getTitle(), gptResponse.getSummary(), gptResponse.getTopic(), gptResponse.getKeywords(), url);
    }
}
