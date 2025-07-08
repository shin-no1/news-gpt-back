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

            exception = getException(wrappedRequest, exception);
            int status = getStatusCode(exception, wrappedResponse);
            String logJson = LogFormatter.formatExceptionJson(trackingId, startTime, endTime, request, wrappedRequest, wrappedResponse, status, exception);
            if (exception != null) {
                log.error("{}", logJson);
                log.error("[{}] Full stack trace", trackingId, exception);
            } else {
                if (status >= 500) {
                    log.error("{}", logJson);
                } else if (status >= 400) {
                    log.warn("{}", logJson);
                } else {
                    log.info("{}", logJson);
                }
            }

            wrappedResponse.copyBodyToResponse();
        }
    }

    protected int getStatusCode(Exception exception, ContentCachingResponseWrapper response) {
        int status;
        if (exception != null) {
            status = 500;
        } else if (response != null) {
            status = response.getStatus();
            if (status < 100 || status > 599) {
                status = 500;
            }
        } else {
            status = 500;
        }
        return status;
    }

    private Exception getException(ContentCachingRequestWrapper request, Exception e) {
        Exception exception = (Exception) request.getAttribute("FILTER_EXCEPTION");
        if (exception != null) {
            return exception;
        }
        return e;
    }
}