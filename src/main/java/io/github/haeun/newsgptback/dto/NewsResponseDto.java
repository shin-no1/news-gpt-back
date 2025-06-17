package io.github.haeun.newsgptback.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NewsResponseDto {
    private String title;
    private String content;
}
