package io.github.haeun.newsgptback.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GptMessageDto {
    private String role;
    private String content;

    public GptMessageDto() {}
    public GptMessageDto(String role, String content) {
        this.role = role;
        this.content = content;
    }
}
