package io.github.haeun.newsgptback.news.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    private String email;
    private String nickname;
    private String password;
}
