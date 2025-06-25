package io.github.haeun.newsgptback.infrastructure.kafka.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestLogMessage {
    private Long userId;
    private String ipAddress;
    private String requestUrl;
    private boolean success;
    private LocalDateTime requestTime;
}
