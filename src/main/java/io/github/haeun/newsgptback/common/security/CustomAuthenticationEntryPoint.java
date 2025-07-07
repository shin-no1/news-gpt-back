package io.github.haeun.newsgptback.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.haeun.newsgptback.common.exception.LogFormatter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;


/**
 * 인증되지 않은 사용자의 접근을 처리하는 커스텀 인증 진입점 클래스.
 * Spring Security에서 인증이 필요한 리소스에 대한 무단 접근을 처리합니다.
 */
@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    /**
     * 인증되지 않은 사용자의 요청을 처리하는 메서드입니다.
     *
     * @param request       HTTP 요청 객체
     * @param response      HTTP 응답 객체
     * @param authException 인증 과정에서 발생한 예외
     * @throws IOException 응답 작성 중 입출력 예외가 발생한 경우
     */
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        String trackingId = MDC.get("trackingId");
        log.warn(LogFormatter.formatExceptionJson(trackingId, authException, request, 401));
        log.error("[{}] Full stack trace", trackingId, authException);

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        Map<String, Object> body = Map.of(
                "status", 401,
                "code", "UNAUTHORIZED",
                "message", "인증이 필요합니다."
        );
        new ObjectMapper().writeValue(response.getWriter(), body);
    }
}