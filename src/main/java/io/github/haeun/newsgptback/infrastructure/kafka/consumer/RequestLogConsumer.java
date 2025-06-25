package io.github.haeun.newsgptback.infrastructure.kafka.consumer;

import io.github.haeun.newsgptback.infrastructure.kafka.dto.RequestLogMessage;
import io.github.haeun.newsgptback.infrastructure.kafka.domain.UserRequestLog;
import io.github.haeun.newsgptback.infrastructure.kafka.domain.UserRequestLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestLogConsumer {

    private final UserRequestLogRepository userRequestLogRepository;

    @KafkaListener(topics = "rate-log", groupId = "request-log-group", containerFactory = "kafkaListenerContainerFactory")
    public void consume(RequestLogMessage message) {
        log.info("[Kafka] 메시지 수신: {}", message);
        UserRequestLog log = UserRequestLog.builder()
                .userId(message.getUserId())
                .ipAddress(message.getIpAddress())
                .requestUrl(message.getRequestUrl())
                .isSuccess(message.isSuccess())
                .requestTime(message.getRequestTime())
                .build();
        userRequestLogRepository.save(log);
    }
}