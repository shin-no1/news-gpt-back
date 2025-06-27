package io.github.haeun.newsgptback.news.domain.emailVerificationLog;

import io.github.haeun.newsgptback.common.enums.EmailVerificationAction;
import io.github.haeun.newsgptback.common.enums.EmailVerificationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "email_verification_log")
public class EmailVerificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Enumerated(EnumType.STRING)
    private EmailVerificationAction action;

    @Enumerated(EnumType.STRING)
    private EmailVerificationStatus status;

    private String reason;

    private String ipAddress;

    @Column(length = 1000)
    private String userAgent;

    private final LocalDateTime createdAt = LocalDateTime.now();

    public EmailVerificationLog(String email, EmailVerificationAction action, EmailVerificationStatus status, String reason, String ipAddress, String userAgent) {
        this.email = email;
        this.action = action;
        this.status = status;
        this.reason = reason;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
    }
}
