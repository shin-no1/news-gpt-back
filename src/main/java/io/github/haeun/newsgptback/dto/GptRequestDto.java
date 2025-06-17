package io.github.haeun.newsgptback.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GptRequestDto {
    private String model;
    private List<GptMessageDto> messages;
    private int max_tokens;
    private double temperature;

    public GptRequestDto(String model, List<GptMessageDto> messages, int max_tokens, double temperature) {
        this.model = model;
        this.messages = messages;
        this.max_tokens = max_tokens;
        this.temperature = temperature;
    }
}
