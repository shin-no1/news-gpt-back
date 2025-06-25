package io.github.haeun.newsgptback.infrastructure.kafka.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRequestLogRepository extends JpaRepository<UserRequestLog, Long> {
}
