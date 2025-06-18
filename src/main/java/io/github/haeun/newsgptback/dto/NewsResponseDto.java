package io.github.haeun.newsgptback.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class NewsResponseDto {
    private String title;
    private String summary;
    private String topic;
    private List<String> keywords;
    private String url;

    public NewsResponseDto(String title, String summary) {
        this.title = title;
        this.summary = summary;
    }
}
