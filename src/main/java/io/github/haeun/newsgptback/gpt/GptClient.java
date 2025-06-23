package io.github.haeun.newsgptback.gpt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.completions.CompletionUsage;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.haeun.newsgptback.dto.GptResponse;
import io.github.haeun.newsgptback.loader.PromptLoader;
import io.github.haeun.newsgptback.record.NewsInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GptClient {
    private final ObjectMapper objectMapper;
    private final OpenAIClient openAIClient;
    private final String systemPrompt;
    private final int maxTokens;
    private final double temperature;
    public final String promptVersion;

    public GptClient(ObjectMapper objectMapper,
                     @Value("${openai.max-tokens}") int maxTokens,
                     @Value("${openai.temperature}") double temperature,
                     @Value("${openai.prompt-version}") String promptVersion) {
        this.objectMapper = objectMapper;
        this.maxTokens = maxTokens;
        this.temperature = temperature;
        this.promptVersion = promptVersion;
        this.systemPrompt = PromptLoader.loadPrompt("summary-v" + this.promptVersion);
        Dotenv dotenv = Dotenv.load();
        String API_KEY = dotenv.get("OPENAI_API_KEY");
        this.openAIClient = OpenAIOkHttpClient.builder()
                .apiKey(API_KEY)
                .build();
    }

    public GptResponse summarize(NewsInfo newsInfo) {
        try {
            long startTime = System.currentTimeMillis();

            ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                    .addSystemMessage(systemPrompt)
                    .addUserMessage("Summarize the following news article using the format defined in the system prompt:\n" + newsInfo.content())
                    .model(ChatModel.GPT_3_5_TURBO)
                    .temperature(temperature)
                    .maxCompletionTokens(maxTokens)
                    .build();

            ChatCompletion chatCompletion = openAIClient.chat().completions().create(params);

            CompletionUsage completionUsage = chatCompletion.usage().get();

            long endTime = System.currentTimeMillis();
            log.info("[CompletionUsage] {}s, prompt_tokens: {}, completion_tokens: {}, total_tokens: {}", (endTime - startTime) / 1000.0, completionUsage.promptTokens(), completionUsage.completionTokens(), completionUsage.totalTokens());

            String json = chatCompletion.choices().get(0).message().content().get();
            return objectMapper.readValue(json, GptResponse.class);

        } catch (Exception e) {
            log.error("[Error]", e);
            return null;
        }
    }


}
