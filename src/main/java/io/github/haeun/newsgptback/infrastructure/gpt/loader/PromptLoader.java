package io.github.haeun.newsgptback.infrastructure.gpt.loader;

import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class PromptLoader {
    public static String loadPrompt(String filename) {
        try {
            ClassPathResource resource = new ClassPathResource("prompts/" + filename);
            return Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("프롬프트 파일을 불러오는 데 실패했습니다", e);
        }
    }

    public static Map<String, String> loadPrompts(String versionFilePath) {
        try {
            Map<String, String> promptMap = new HashMap<>();

            ClassPathResource resource = new ClassPathResource("prompts/" + versionFilePath);
            if (!resource.exists()) {
                throw new RuntimeException("프롬프트 파일을 찾을 수 없음: " + resource.getPath());
            }
//            String content = Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);
            String content;
            try (InputStream inputStream = resource.getInputStream()) {
                content = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));
            }

            String[] sections = content.split("=== ");
            for (String section : sections) {
                if (section.startsWith("REQUEST_PROMPT ===")) {
                    promptMap.put("request", section.replaceFirst("REQUEST_PROMPT ===", "").trim());
                } else if (section.startsWith("STRUCTURED_SUMMARY_PROMPT ===")) {
                    promptMap.put("summary", section.replaceFirst("STRUCTURED_SUMMARY_PROMPT ===", "").trim());
                }
            }
            return promptMap;
        } catch (IOException e) {
            throw new UncheckedIOException("프롬프트 파일을 불러오는 데 실패했습니다", e);
        }
    }
}
