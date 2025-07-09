package io.github.haeun.newsgptback.news.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookmarkSaveResponse {
    private long bookmarkId;
    private long groupId;
}
