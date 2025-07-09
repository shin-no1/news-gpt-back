package io.github.haeun.newsgptback.news.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BookmarkRequest {
    private Long summaryHistoryId;
    private Long groupId;
}
