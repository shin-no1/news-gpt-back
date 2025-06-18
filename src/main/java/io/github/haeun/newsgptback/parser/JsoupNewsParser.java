package io.github.haeun.newsgptback.parser;

import io.github.haeun.newsgptback.dto.NewsResponse;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class JsoupNewsParser {
    public NewsResponse parse(String url) {
        try {
            if (!url.startsWith("https://n.news.naver.com/")) throw new RuntimeException("네이버 뉴스가 아닙니다.");

            Document doc = Jsoup.connect(toPrintUrl(url)).get();
            String title = doc.select("#title_area").text();
            String content = doc.select("#dic_area").text();
            return new NewsResponse(title, content);
        } catch (IOException e) {
            log.error("[Error]", e);
        }
        return null;
    }

    private String toPrintUrl(String originalUrl) {
        if (originalUrl.contains("/article/print/")) {
            return originalUrl;
        }
        return originalUrl.replaceFirst("/article/", "/article/print/");
    }
}
