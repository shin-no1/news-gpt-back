package io.github.haeun.newsgptback.common.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class RateLimiterTest {

    @Autowired
    private RateLimiter rateLimiter;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private String testKey;

    @BeforeEach
    void setUp() {
        String ip = "127.0.0.1";
        String date = LocalDate.now().toString();
        testKey = "rate:" + ip + ":" + date;
        redisTemplate.delete(testKey);
    }

    // 키가 존재하지 않을 때, 카운트는 0을 반환해야 함
    @Test
    void shouldReturnZeroWhenKeyDoesNotExist() {
        long count = rateLimiter.getCurrentCount(testKey);
        assertEquals(0L, count);
    }

    // 키가 존재할 때, 해당 값을 정상적으로 반환해야 함
    @Test
    void shouldReturnExistingCountWhenKeyExists() {
        redisTemplate.opsForValue().set(testKey, "3");
        long count = rateLimiter.getCurrentCount(testKey);
        assertEquals(3L, count);
    }

    // 키가 없을 경우 increment 시 1이 되고, TTL이 설정되어야 함
    @Test
    void shouldSetTtlWhenKeyIsIncrementedFirstTime() {
        rateLimiter.incrementWithTtlIfNeeded(testKey, 60);
        String value = redisTemplate.opsForValue().get(testKey);
        assertEquals("1", value);
        Long ttl = redisTemplate.getExpire(testKey);
        assertTrue(ttl > 0 && ttl <= 60);
    }

    // 키가 이미 존재하면 TTL은 덮어쓰지 않고 유지되어야 함
    @Test
    void shouldNotOverrideTtlWhenKeyAlreadyExists() {
        redisTemplate.opsForValue().set(testKey, "2");
        redisTemplate.expire(testKey, Duration.ofSeconds(100));

        rateLimiter.incrementWithTtlIfNeeded(testKey, 60);

        String value = redisTemplate.opsForValue().get(testKey);
        assertEquals("3", value);

        Long ttl = redisTemplate.getExpire(testKey);
        assertTrue(ttl > 60); // 기존 TTL 유지됨
    }
}