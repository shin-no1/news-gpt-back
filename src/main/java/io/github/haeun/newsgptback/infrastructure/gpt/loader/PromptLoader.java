package io.github.haeun.newsgptback.infrastructure.gpt.loader;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class PromptLoader {
    public static String loadPrompt(String filename) {
        try {
            ClassPathResource resource = new ClassPathResource("prompts/" + filename);
            return Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("프롬프트 파일을 불러오는 데 실패했습니다", e);
        }
    }
}
