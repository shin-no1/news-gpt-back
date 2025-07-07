package io.github.haeun.newsgptback.common.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * 각 HTTP 요청에 대해 고유한 추적 ID를 MDC(Mapped Diagnostic Context)에 추가하는 필터
 * 이를 통해 로그 추적 및 디버깅이 용이해집니다.
 */
@Component
public class MdcFilter extends OncePerRequestFilter {
    /**
     * 각 요청마다 실행되는 필터 메소드
     * UUID를 생성하여 MDC에 'trackingId'로 저장하고, 요청 처리 후 MDC를 정리합니다.
     *
     * @param request     HTTP 요청 객체
     * @param response    HTTP 응답 객체
     * @param filterChain 필터 체인
     * @throws ServletException 서블릿 처리 중 발생할 수 있는 예외
     * @throws IOException      입출력 처리 중 발생할 수 있는 예외
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String trackingId = UUID.randomUUID().toString();
        MDC.put("trackingId", trackingId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
