package io.github.haeun.newsgptback.common.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.haeun.newsgptback.common.exception.LogFormatter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 사용자의 접근이 거부되었을 때 처리하는 핸들러.
 * Spring Security의 AccessDeniedHandler 인터페이스를 구현하여 권한이 없는 리소스에 대한 접근 시도를 처리합니다.
 */
@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    /**
     * 접근 거부된 예외를 처리하고 JSON 형식의 에러 메시지로 응답합니다.
     * 사용자가 권한이 없는 리소스에 접근을 시도할 때 호출됩니다.
     *
     * @param request 접근이 거부된 HttpServletRequest
     * @param response 클라이언트에게 에러 상세 내용을 전송할 HttpServletResponse
     * @param accessDeniedException 접근 거부 상세 정보를 포함한 예외
     * @throws IOException 입출력 예외 발생 시
     */
    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        int status = HttpServletResponse.SC_FORBIDDEN;
        String trackingId = MDC.get("trackingId");
        log.warn(LogFormatter.formatExceptionJson(trackingId, accessDeniedException, request, status));
        log.error("[{}] Full stack trace", trackingId, accessDeniedException);

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        Map<String, Object> body = Map.of(
                "status", status,
                "code", "FORBIDDEN",
                "message", "접근 권한이 없습니다."
        );
        new ObjectMapper().writeValue(response.getWriter(), body);
    }
}
