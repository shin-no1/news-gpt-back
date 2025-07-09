package io.github.haeun.newsgptback.news.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BookmarkMultiPutRequest {
    private List<Long> bookmarkIds;
    private Long groupId;
    private Long afterGroupId;
}
