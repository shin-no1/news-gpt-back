package io.github.haeun.newsgptback.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RateLimiter {
    private final StringRedisTemplate redisTemplate;

    /**
     * 요금 제한 정책에 따라 작업이 허용되는지 여부를 결정합니다.
     * 이 메서드는 Redis 에서 주어진 키와 연결된 카운터를 증가시킵니다
     * 카운터 값이 지정된 한도 내에 있는지 확인합니다.
     *
     * @param key        제한에 사용되는 키
     * @param limit      TTL 창 내에서 허용되는 최대 작업 수를 제한합니다
     * @param ttlSeconds 실행 시간(TTL)을 초 단위로 나타냅니다
     * @return 허용되면 true, 제한을 초과하면 false 반환
     */
    public boolean isAllowed(String key, long limit, long ttlSeconds) {
        Long current = redisTemplate.opsForValue().increment(key);
        if (current != null && current == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(ttlSeconds));
        }
        return current != null && current <= limit;
    }
}
