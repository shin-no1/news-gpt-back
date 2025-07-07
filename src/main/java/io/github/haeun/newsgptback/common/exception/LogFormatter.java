package io.github.haeun.newsgptback.common.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

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
        long durationMs = (startTime != null && endTime != null)
                ? Duration.between(startTime, endTime).toMillis()
                : -1;

        String method = safe(request.getMethod());
        String uri = safe(request.getRequestURI());
        String query = safe(request.getQueryString());
        String clientIp = safe(request.getRemoteAddr());
        String userAgent = safe(request.getHeader("User-Agent"));

        String requestBody = getFilteredRequestBody(wrappedRequest);
        String responseBody = getResponseBody(wrappedResponse);
        String exceptionField = getExceptionField(e);

        return String.format("""
                        {
                          "trackingId": "%s",
                          "startTime": %s,
                          "endTime": %s,
                          "durationMs": %d,
                          "method": %s,
                          "status": %d,
                          "uri": %s,
                          "query": %s,
                          "clientIp": %s,
                          "userAgent": %s,
                          "headers": "%s",
                          "requestBody": %s,
                          "responseBody": %s,
                          "exception": %s
                        }
                        """,
                trackingId,
                safe(startTime != null ? startTime.toString() : null),
                safe(endTime != null ? endTime.toString() : null),
                durationMs,
                method,
                status,
                uri,
                query,
                clientIp,
                userAgent,
                getSafeHeaders(request),
                requestBody,
                responseBody,
                exceptionField
        );
    }

    private static String safe(String value) {
        return value != null ? "\"" + value + "\"" : "null";
    }

    private static String getSafeHeaders(HttpServletRequest request) {
        final Set<String> ALLOWED_HEADER_KEYS = Set.of(
                "x-device-id", "referer", "origin", "authorization"
        );
        final Set<String> MASKED_HEADER_KEYS = Set.of(
                "authorization"
        );
        if (request == null) {
            return "null";
        }
        Map<String, String> filteredHeaders = new LinkedHashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames == null) {
            return "null";
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
                return "null";
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
                return "null";
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
        return String.format("""
                {
                  "type": "%s",
                  "message": %s,
                  "stackTrace": "%s"
                }
                """, e.getClass().getName(), safe(e.getMessage()), Arrays.stream(e.getStackTrace())
                .limit(5)
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\\n")) + "\\n...");
    }

    private static String maskSensitiveFields(String json) {
        for (String key : SENSITIVE_FIELDS) {
            json = json.replaceAll("(?i)(\"" + key + "\"\\s*:\\s*\")[^\"]+\"", "$1*****\"");
        }
        return json;
    }

}
