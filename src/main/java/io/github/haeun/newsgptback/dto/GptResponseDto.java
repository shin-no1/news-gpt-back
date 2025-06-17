package io.github.haeun.newsgptback.dto;

import java.util.List;

public class GptResponseDto {
    private List<Choice> choices;

    public List<Choice> getChoices() {
        return choices;
    }

    public static class Choice {
        private GptMessageDto message;

        public GptMessageDto getMessage() {
            return message;
        }

        public void setMessage(GptMessageDto message) {
            this.message = message;
        }
    }
}
