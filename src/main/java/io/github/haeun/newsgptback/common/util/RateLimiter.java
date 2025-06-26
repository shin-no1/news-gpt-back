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
     * Redis에서 지정된 키와 관련된 현재 카운트 값을 검색
     * 키가 존재하지 않거나 값이 null인 경우 메서드는 0을 반환
     *
     * @param key Redis에서 검색할 카운트 값과 연결된 키
     * @return 지정된 키와 연결된 현재 카운트, 키가 존재하지 않는 경우 0
     */
    public long getCurrentCount(String key) {
        String value = redisTemplate.opsForValue().get(key);
        return value == null ? 0 : Long.parseLong(value);
    }

    /**
     * Redis에서 지정된 키와 관련된 값을 증가
     * 증가된 값이 1일 경우 해당 키에 대한 만료 시간(TTL)을 설정
     * 이 메서드는 일반적으로 속도 제한 컨텍스트에서 카운터를 초기화하고 업데이트하는 데 사용
     *
     * @param key        Redis에서 증가시킬 키
     * @param ttlSeconds 키의 값이 1이 되었을 때 설정할 만료 시간(초)
     */
    public void incrementWithTtlIfNeeded(String key, long ttlSeconds) {
        Long current = redisTemplate.opsForValue().increment(key);
        if (current != null && current == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(ttlSeconds));
        }
    }

}
