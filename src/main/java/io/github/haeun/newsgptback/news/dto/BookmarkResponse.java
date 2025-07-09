package io.github.haeun.newsgptback.news.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BookmarkResponse {
    private Long id;
    private Long groupId;

    private String url;
    private String title;
    private String summary;
    private String topic;
    private List<String> keywords;
}
