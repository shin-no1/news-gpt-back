package io.github.haeun.newsgptback.news.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BookmarkPutRequest {
    private Long groupId;
    private Long afterGroupId;
}
