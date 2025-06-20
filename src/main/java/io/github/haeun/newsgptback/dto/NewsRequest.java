package io.github.haeun.newsgptback.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NewsRequest {
    @Schema(description = "분석할 뉴스 기사 URL", example = "https://n.news.naver.com/article/015/0005146248")
    private String url;

    public NewsRequest(String url) {
        this.url = url;
    }
}
