package io.github.haeun.newsgptback.parser;

import io.github.haeun.newsgptback.dto.NewsResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JsoupNewsParser {
    public NewsResponse parse(String url) {
        try {
            if (!url.startsWith("https://n.news.naver.com/")) {
                throw new RuntimeException("네이버 뉴스가 아닙니다.");
            }

            Document doc = Jsoup.connect(url).get();
            String title = doc.title();
            String content = doc.select("#dic_area").text();
            return new NewsResponse(title, content);
        } catch (IOException e) {
            throw new RuntimeException("뉴스 파싱 실패", e);
        }
    }
}
