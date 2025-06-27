package io.github.haeun.newsgptback.news.domain.emailVerificationLog;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationLogRepository extends JpaRepository<EmailVerificationLog, Long> {
}
