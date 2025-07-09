package io.github.haeun.newsgptback.news.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookmarkGroupsResponse {
    private Long id;
    private Long displayOrder;
    private String name;
}
