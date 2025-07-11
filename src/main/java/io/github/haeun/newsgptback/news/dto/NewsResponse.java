package io.github.haeun.newsgptback.news.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class NewsResponse {
    @Schema(description = "뉴스 제목")
    private String title;

    @Schema(description = "요약된 본문")
    private String summary;

    @Schema(description = "주제 (예: 정치, 사회 등)")
    private String topic;

    @Schema(description = "핵심 키워드 리스트")
    private List<String> keywords;

    @Schema(description = "요약 대상 뉴스 URL")
    private String url;

    @Schema(description = "요약 번호")
    private long summaryHistoryId;

    public NewsResponse(String title, String summary, String topic, List<String> keywords, String url) {
        this.title = title;
        this.summary = summary;
        this.topic = topic;
        this.keywords = keywords;
        this.url = url;
    }

    public void setSummaryHistoryId(long summaryHistoryId) {
        this.summaryHistoryId = summaryHistoryId;
    }
}
