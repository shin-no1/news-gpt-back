package io.github.haeun.newsgptback.common.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.haeun.newsgptback.news.domain.user.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class LogFormatter {

    private static final Set<String> SENSITIVE_FIELDS = Set.of("password", "accessToken", "refreshToken");

    public static String formatExceptionJson(String trackingId, Exception e, HttpServletRequest request, int status) {
        return formatExceptionJson(trackingId, null, null, request, null, null, status, e);
    }

    public static String formatExceptionJson(String trackingId, Instant startTime, Instant endTime,
                                             HttpServletRequest request,
                                             ContentCachingRequestWrapper wrappedRequest,
                                             ContentCachingResponseWrapper wrappedResponse,
                                             int status,
                                             Exception e) {
        Long durationMs = (startTime != null && endTime != null)
                ? Duration.between(startTime, endTime).toMillis()
                : null;

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        String clientIp = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        String requestBody = getFilteredRequestBody(wrappedRequest);
        String responseBody = getResponseBody(wrappedResponse);
        String exceptionField = getExceptionField(e);

        Map<String, Object> logMap = new LinkedHashMap<>();
        logMap.put("trackingId", trackingId);
        logMap.put("userId", getUserId());
        logMap.put("method", method);
        logMap.put("uri", uri);
        logMap.put("status", status);
        logMap.put("durationMs", durationMs);
        logMap.put("clientIp", clientIp);
        logMap.put("userAgent", userAgent);
        logMap.put("query", query);
        logMap.put("headers", getSafeHeaders(request));
        logMap.put("requestBody", requestBody);
        logMap.put("responseBody", responseBody);
        logMap.put("startTime", startTime != null ? startTime.toString() : null);
        logMap.put("endTime", endTime != null ? endTime.toString() : null);
        logMap.put("exception", exceptionField);

        try {
            return new ObjectMapper().writeValueAsString(logMap);
        } catch (Exception exception) {
            log.error("ERROR!", exception);
            return null;
        }

//        return String.format("""
//                        {
//                          "trackingId": "%s",
//
//                          "userId": %d,
//
//                          "method": %s,
//                          "uri": %s,
//                          "status": %d,
//                          "durationMs": %d,
//
//                          "clientIp": %s,
//                          "userAgent": %s,
//
//                          "query": %s,
//                          "headers": "%s",
//
//                          "requestBody": %s,
//                          "responseBody": %s,
//
//                          "startTime": %s,
//                          "endTime": %s,
//
//                          "exception": %s
//                        }
//                        """,
//                trackingId,
//                getUserId(),
//                method,
//                uri,
//                status,
//                durationMs,
//                clientIp,
//                userAgent,
//                query,
//                getSafeHeaders(request),
//                requestBody,
//                responseBody,
//                safe(startTime != null ? startTime.toString() : null),
//                safe(endTime != null ? endTime.toString() : null),
//                exceptionField
//        );
    }

//    private static String safe(String value) {
//        return value != null ? "\"" + value + "\"" : null;
//    }

    private static String getSafeHeaders(HttpServletRequest request) {
        final Set<String> ALLOWED_HEADER_KEYS = Set.of(
                "x-device-id", "referer", "origin", "authorization"
        );
        final Set<String> MASKED_HEADER_KEYS = Set.of(
                "authorization"
        );
        if (request == null) {
            return null;
        }
        Map<String, String> filteredHeaders = new LinkedHashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames == null) {
            return null;
        }
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (!ALLOWED_HEADER_KEYS.contains(headerName.toLowerCase())) {
                continue;
            }
            String value = request.getHeader(headerName);
            if (MASKED_HEADER_KEYS.contains(headerName.toLowerCase())) {
                filteredHeaders.put(headerName, "***");
            } else {
                filteredHeaders.put(headerName, value);
            }
        }
        try {
            return new ObjectMapper().writeValueAsString(filteredHeaders);
        } catch (JsonProcessingException e) {
            return "\"<unreadable headers>\"";
        }
    }

    private static String getFilteredRequestBody(ContentCachingRequestWrapper request) {
        try {
            if (request == null || request.getContentAsByteArray().length == 0) {
                return null;
            }
            String body = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
            return maskSensitiveFields(body);
        } catch (Exception e) {
            return "\"<unreadable>\"";
        }
    }

    private static String getResponseBody(ContentCachingResponseWrapper response) {
        try {
            if (response == null || response.getContentAsByteArray().length == 0) {
                return null;
            }
            String body = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);
            return body.length() > 2000 ? "\"<too large>\"" : body;
        } catch (Exception e) {
            return "\"<unreadable>\"";
        }
    }

    private static String getExceptionField(Exception e) {
        if (e == null) {
            return "null";
        }
        Map<String, Object> logMap = new LinkedHashMap<>();
        logMap.put("type", e.getClass().getName());
        logMap.put("message", e.getMessage());
        logMap.put("stackTrace", Arrays.stream(e.getStackTrace())
                .limit(5)
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\\n")) + "\\n...");
        try {
            return new ObjectMapper().writeValueAsString(logMap);
        } catch (Exception exception) {
            log.error("ERROR!", exception);
            return null;
        }
//        return String.format("""
//                {
//                  "type": "%s",
//                  "message": %s,
//                  "stackTrace": "%s"
//                }
//                """, e.getClass().getName(), safe(e.getMessage()), Arrays.stream(e.getStackTrace())
//                .limit(5)
//                .map(StackTraceElement::toString)
//                .collect(Collectors.joining("\\n")) + "\\n...");
    }

    private static String maskSensitiveFields(String json) {
        for (String key : SENSITIVE_FIELDS) {
            json = json.replaceAll("(?i)(\"" + key + "\"\\s*:\\s*\")[^\"]+\"", "$1*****\"");
        }
        return json;
    }

    private static Long getUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return null;
            }
            Object principal = authentication.getPrincipal();
            if (principal instanceof User user) {
                return user.getId();
            }
            return null;
        } catch (Exception e) {
            return null; // 인증 정보가 없거나 에러 시 null 처리
        }
    }

}
