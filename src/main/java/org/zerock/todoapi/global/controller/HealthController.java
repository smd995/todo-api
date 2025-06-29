package org.zerock.todoapi.global.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.todoapi.domain.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class HealthController {

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    private final UserRepository userRepository;

    @GetMapping("/api/health")
    public ResponseEntity<Map<String, Object>> health() {
        log.info("=== Health check 요청 받음 ===");

        try {
            // DB 연결 테스트 (간단한 쿼리 실행)
            long userCount = userRepository.count();
            log.info("DB 연결 성공 - 현재 유저 수: {}", userCount);

            Map<String, Object> status = Map.of(
                    "status", "UP",
                    "timestamp", LocalDateTime.now(),
                    "profile", activeProfile,
                    "database", "Connected",
                    "userCount", userCount,
                    "message", "TodoAPI is running with RDS"
            );

            log.info("Health check 응답: {}", status);
            return ResponseEntity.ok(status);

        } catch (Exception e) {
            log.error("DB 연결 실패: {}", e.getMessage());

            Map<String, Object> status = Map.of(
                    "status", "DOWN",
                    "timestamp", LocalDateTime.now(),
                    "profile", activeProfile,
                    "database", "Disconnected",
                    "error", e.getMessage(),
                    "message", "Database connection failed"
            );

            return ResponseEntity.status(500).body(status);
        }
    }
}