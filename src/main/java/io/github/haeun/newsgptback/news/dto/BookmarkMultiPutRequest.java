package io.github.haeun.newsgptback.news.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkMultiPutRequest {
    private List<Long> bookmarkIds;
    private Long groupId;
    private Long afterGroupId;
}
