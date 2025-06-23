package io.github.haeun.newsgptback.parser;

import io.github.haeun.newsgptback.record.NewsInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class JsoupNewsParser {
    /**
     * 뉴스 기사 HTML을 Jsoup으로 파싱하여 본문 텍스트를 추출
     *
     * @param url 크롤링할 기사 URL
     * @return 추출된 기사 본문 텍스트
     */
    public NewsInfo parse(String url) {
        try {
            if (!url.startsWith("https://n.news.naver.com/")) throw new RuntimeException("네이버 뉴스가 아닙니다.");

            Document doc = Jsoup.connect(toPrintUrl(url)).get();
            String title = doc.select("#title_area").text();
            String content = doc.select("#dic_area").text();
            return new NewsInfo(title, content, url);
        } catch (IOException e) {
            log.error("[Error]", e);
        }
        return null;
    }

    private String toPrintUrl(String originalUrl) {
        if (originalUrl.contains("/article/print/")) {
            return originalUrl;
        }
        if (originalUrl.contains("/hotissue/article/")) {
            return originalUrl.replaceFirst("/hotissue/article/", "/article/print/");
        }
        return originalUrl.replaceFirst("/article/", "/article/print/");
    }
}
