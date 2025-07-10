package io.github.haeun.newsgptback.config;

import io.github.haeun.newsgptback.common.util.JwtTestUtil;
import io.github.haeun.newsgptback.common.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestJwtConfig {

    @Autowired
    private JwtUtil jwtUtil;

    @Bean
    public JwtTestUtil jwtTestUtil() {
        return new JwtTestUtil(jwtUtil);
    }
}
