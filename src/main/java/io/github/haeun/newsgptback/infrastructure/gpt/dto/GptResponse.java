package io.github.haeun.newsgptback.infrastructure.gpt.dto;

import lombok.Data;

import java.util.List;

@Data
public class GptResponse {
    private String summary;
    private String topic;
    private List<String> keywords;
}
