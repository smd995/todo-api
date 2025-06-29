package org.zerock.todoapi.domain.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.todoapi.domain.user.dto.CreateUserRequest;
import org.zerock.todoapi.domain.user.entity.User;
import org.zerock.todoapi.domain.user.repository.UserRepository;
import org.zerock.todoapi.domain.user.service.UserService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Slf4j
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("유저 생성 전체 플로우 테스트")
    void createUserIntegration() throws Exception {
        log.info("=== 유저 생성 통합 테스트 시작 ===");
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
                .name("통합테스트유저")
                .email("integration@example.com")
                .build();

        // When - API 호출
        log.info("POST /api/users API 호출 시작");
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("통합테스트유저"))
                .andExpect(jsonPath("$.email").value("integration@example.com"))
                .andExpect(jsonPath("$.id").exists());

        log.info("API 호출 완료 - 응답 검증 성공");

        // Then - DB 검증
        log.info("데이터베이스 검증 시작");
        Optional<User> savedUser = userRepository.findByEmail("integration@example.com");
        log.info("DB 조회 결과: {}", savedUser.isPresent() ? "찾음" : "못찾음");

        assertThat(savedUser).isPresent();
        assertThat(savedUser.get().getName()).isEqualTo("통합테스트유저");
        assertThat(savedUser.get().getEmail()).isEqualTo("integration@example.com");

        log.info("저장된 유저 정보: {}", savedUser.get());
        log.info("=== 유저 생성 통합 테스트 완료 ===");
    }

    @Test
    @DisplayName("중복 이메일로 유저 생성 실패 테스트")
    void createUser_DuplicateEmail_Integration() throws Exception {
        log.info("=== 중복 이메일 유저 생성 실패 테스트 시작 ===");

        // Given - 기존 유저 저장
        User existingUser = User.builder()
                .name("기존유저")
                .email("duplicate@example.com")
                .build();

        User savedExistingUser = userRepository.save(existingUser);
        log.info("기존 유저 저장 완료: {}", savedExistingUser);

        CreateUserRequest request = CreateUserRequest.builder()
                .name("새유저")
                .email("duplicate@example.com") // 동일한 이메일
                .build();

        log.info("중복 이메일로 새 유저 생성 요청: {}", request);

        // When & Then - 예외 발생 검증 (실제 API 호출 없이)
        log.info("UserService에서 중복 이메일 예외 검증");

        // UserService를 직접 호출해서 예외 검증
        assertThatThrownBy(() -> {
            // 이미 존재하는 이메일로 유저 생성 시도
            userService.createUser(request);
        })
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 이메일입니다.");

        log.info("예상대로 IllegalArgumentException 발생");

        // DB에서 기존 유저만 존재하는지 확인
        long userCount = userRepository.count();
        log.info("현재 DB의 유저 수: {}", userCount);
        assertThat(userCount).isEqualTo(1L);

        log.info("=== 중복 이메일 테스트 완료 ===");
    }

    @Test
    @DisplayName("유효성 검증 실패 테스트")
    void createUser_ValidationFailure_Integration() throws Exception {
        log.info("=== 유효성 검증 실패 테스트 시작 ===");

        // Given - 잘못된 요청 데이터
        CreateUserRequest invalidRequest = CreateUserRequest.builder()
                .name("") // 빈 이름
                .email("invalid-email") // 잘못된 이메일 형식
                .build();

        log.info("잘못된 요청 데이터 생성: name='{}', email='{}'", invalidRequest.getName(), invalidRequest.getEmail());

        // When & Then
        log.info("유효성 검증 실패 API 호출 시작");
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        log.info("예상대로 400 Bad Request 응답 받음");

        // DB에 저장되지 않았는지 확인
        long userCount = userRepository.count();
        log.info("현재 DB의 유저 수: {}", userCount);
        assertThat(userCount).isEqualTo(0L);

        log.info("=== 유효성 검증 실패 테스트 완료 ===");
    }
}