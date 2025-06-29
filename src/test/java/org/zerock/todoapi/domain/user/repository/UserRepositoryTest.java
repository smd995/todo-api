package org.zerock.todoapi.domain.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.zerock.todoapi.domain.user.entity.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
@DataJpaTest
@ActiveProfiles("test")
@Slf4j  // Lombok 로깅
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("이메일로 유저 찾기")
    void findByEmail() {
        log.info("=== 이메일로 유저 찾기 테스트 시작 ===");

        // Given
        User user = User.builder()
                .name("테스트유저")
                .email("test@example.com")
                .build();

        log.info("테스트 유저 생성: {}", user);
        entityManager.persistAndFlush(user);
        log.info("유저 저장 완료");

        entityManager.clear();
        log.info("1차 캐시 클리어 완료");

        // When
        log.info("이메일로 유저 검색 시작: test@example.com");
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // Then
        log.info("검색 결과: {}", found.isPresent() ? "찾음" : "못찾음");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("테스트유저");
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");

        log.info("=== 테스트 완료 ===");
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 찾기")
    void findByEmail_NotFound() {
        // When
        log.info("이메일로 유저 검색 시작: notfound@example.com");
        Optional<User> found = userRepository.findByEmail("notfound@example.com");

        // Then
        log.info("검색 결과: {}", found.isPresent() ? "찾음" : "못찾음");
        assertThat(found).isNotPresent();
        log.info("=== 테스트 완료 ===");
    }
}