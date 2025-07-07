package io.github.haeun.newsgptback.common.filter;

import io.github.haeun.newsgptback.common.exception.LogFormatter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.Instant;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 요청, 응답 내용을 캐싱 가능하게 감싸기
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        Instant startTime = Instant.now();
        Exception exception = null;

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            String trackingId = MDC.get("trackingId");
            Instant endTime = Instant.now();

            String logJson = LogFormatter.formatExceptionJson(trackingId, startTime, endTime, request, wrappedRequest, wrappedResponse, exception);
            int status = LogFormatter.getStatusCode(exception, wrappedResponse);
            if (exception != null) {
                log.error(logJson);
                log.error("[{}] Full stack trace", trackingId, exception);
            } else {
                if (status >= 500) {
                    log.error(logJson);
                } else if (status >= 400) {
                    log.warn(logJson);
                } else {
                    log.info(logJson);
                }
            }

            wrappedResponse.copyBodyToResponse();
        }
    }
}