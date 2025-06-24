package io.github.haeun.newsgptback.common.parser;

import io.github.haeun.newsgptback.news.model.NewsInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

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
            Document doc = Jsoup.connect(toPrintUrl(url)).get();
            String title = doc.select("#title_area").text();
            String content = doc.select("#dic_area").text();
            return new NewsInfo(title, content, url);
        } catch (Exception e) {
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
