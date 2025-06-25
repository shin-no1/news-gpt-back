package io.github.haeun.newsgptback.infrastructure.kafka.producer;

import io.github.haeun.newsgptback.infrastructure.kafka.dto.RequestLogMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestLogProducer {

    private final KafkaTemplate<String, RequestLogMessage> kafkaTemplate;

    public void sendLog(RequestLogMessage message) {
        log.info("[Kafka] 로그 전송 시작: {}", message);
        CompletableFuture<SendResult<String, RequestLogMessage>> future =
                kafkaTemplate.send("rate-log", message);
        future.thenAccept(result -> {
            log.info("[Kafka] 전송 성공: offset={}, partition={}",
                    result.getRecordMetadata().offset(),
                    result.getRecordMetadata().partition());
        }).exceptionally(ex -> {
            log.error("[Kafka] 전송 실패", ex);
            return null;
        });
    }
}