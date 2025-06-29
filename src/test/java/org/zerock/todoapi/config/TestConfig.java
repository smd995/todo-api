package org.zerock.todoapi.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@TestConfiguration
@ActiveProfiles("test")
public class TestConfig {

    @Bean
    @Primary
    public Clock testClock() {
        return Clock.fixed(
                LocalDateTime.of(2024, 1, 1, 0, 0, 0)
                        .toInstant(ZoneOffset.UTC),
                ZoneOffset.UTC
        );
    }
}