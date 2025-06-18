package io.github.haeun.newsgptback.gpt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.haeun.newsgptback.dto.GptMessageDto;
import io.github.haeun.newsgptback.dto.GptRequestDto;
import io.github.haeun.newsgptback.dto.GptResponseDto;
import io.github.haeun.newsgptback.loader.PromptLoader;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class GptClient {
    @Autowired
    private ObjectMapper mapper;

    private final String apiKey;

    public GptClient() {
        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("OPENAI_API_KEY");
    }

    @Value("${openai.model}")
    private String model;

    @Value("${openai.max-tokens}")
    private int maxTokens;

    @Value("${openai.temperature}")
    private double temperature;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    String systemPrompt = PromptLoader.loadPrompt("summary");

    public String summarize(String articleText){
        try {
            OkHttpClient client = new OkHttpClient();

            List<GptMessageDto> messages = List.of(
                    new GptMessageDto("system", systemPrompt),
                    new GptMessageDto("user", articleText)
            );

            GptRequestDto gptRequest = new GptRequestDto(model, messages, maxTokens, temperature);

            RequestBody body = RequestBody.create(
                    mapper.writeValueAsString(gptRequest),
                    MediaType.get("application/json")
            );

            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("GPT 요청 실패: " + response.code());
                String responseBody = response.body().string();
                log.info(responseBody);
                GptResponseDto gptResponse = mapper.readValue(responseBody, GptResponseDto.class);
                return gptResponse.getChoices().get(0).getMessage().getContent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
