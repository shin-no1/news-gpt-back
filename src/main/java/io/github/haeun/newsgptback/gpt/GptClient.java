package io.github.haeun.newsgptback.gpt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.StructuredChatCompletion;
import com.openai.models.chat.completions.StructuredChatCompletionCreateParams;
import com.openai.models.completions.CompletionUsage;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.haeun.newsgptback.dto.GptResponseDto;
import io.github.haeun.newsgptback.loader.PromptLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GptClient {
    private final String API_KEY;

    @Autowired
    private ObjectMapper objectMapper;

    public GptClient() {
        Dotenv dotenv = Dotenv.load();
        this.API_KEY = dotenv.get("OPENAI_API_KEY");
    }

    @Value("${openai.max-tokens}")
    private int maxTokens;

    @Value("${openai.temperature}")
    private double temperature;

    String systemPrompt = PromptLoader.loadPrompt("summary");

    public GptResponseDto summarize(String articleText){
        try {
            OpenAIClient client = OpenAIOkHttpClient.builder()
                    .apiKey(API_KEY)
                    .build();

            ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                    .addUserMessage("Summarize the following news article using the format defined in the system prompt: \n" + articleText)
                    .addSystemMessage(systemPrompt)
                    .model(ChatModel.GPT_3_5_TURBO)
                    .temperature(temperature)
                    .maxCompletionTokens(maxTokens)
                    .build();

            ChatCompletion chatCompletion = client.chat().completions().create(params);

            CompletionUsage completionUsage = chatCompletion.usage().get();
            log.info("[CompletionUsage] prompt_tokens: {}, completion_tokens: {}, total_tokens: {}", completionUsage.promptTokens(), completionUsage.completionTokens(), completionUsage.totalTokens());

            String json = chatCompletion.choices().get(0).message().content().get();
            return objectMapper.readValue(json, GptResponseDto.class);

        } catch (Exception e) {
            log.error("[Error]", e);
        }
        return null;
    }
}
