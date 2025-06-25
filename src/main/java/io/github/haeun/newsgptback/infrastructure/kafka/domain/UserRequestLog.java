package io.github.haeun.newsgptback.infrastructure.kafka.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "user_request_log")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;          // 로그인 사용자 ID (nullable)
    private String ipAddress;     // 요청자 IP
    private String requestUrl;    // 요청 URL
    private Boolean isSuccess;    // 성공 여부
    private LocalDateTime requestTime;
}