package io.github.haeun.newsgptback.dto;

import lombok.Data;

import java.util.List;

@Data
public class GptResponseDto {
    private String summary;
    private String topic;
    private List<String> keywords;
}
