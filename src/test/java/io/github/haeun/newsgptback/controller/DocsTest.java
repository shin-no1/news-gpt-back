package io.github.haeun.newsgptback.controller;

import io.github.haeun.newsgptback.common.enums.ErrorCode;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DocsTest {

    @Test
    void generateErrorCodeAdoc() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append("|===\n");
        sb.append("| 코드 | 이름 | 설명\n\n");

        for (ErrorCode code : ErrorCode.values()) {
            sb.append("| ").append(code.getHttpStatus())
                    .append("| ").append(code.name())
                    .append(" | ").append(code.getMessage()).append("\n");
        }

        sb.append("|===\n");

        Path outputPath = Paths.get("target/generated-snippets/error-codes/generated-error-codes.adoc");
        Files.createDirectories(outputPath.getParent());
        Files.writeString(outputPath, sb.toString());
    }
}
