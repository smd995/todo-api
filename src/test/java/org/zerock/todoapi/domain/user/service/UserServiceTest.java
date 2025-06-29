package org.zerock.todoapi.domain.user.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.zerock.todoapi.domain.user.dto.CreateUserRequest;
import org.zerock.todoapi.domain.user.dto.UserResponse;
import org.zerock.todoapi.domain.user.entity.User;
import org.zerock.todoapi.domain.user.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("유저 생성 성공")
    void createUser_Success() {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
                .name("테스트유저")
                .email("test@example.com")
                .build();

        User savedUser = User.builder()
                .id(1L)
                .name("테스트유저")
                .email("test@example.com")
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        UserResponse response = userService.createUser(request);

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("테스트유저");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("중복 이메일로 유저 생성 실패")
    void createUser_DuplicateEmail() {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
                .name("테스트유저")
                .email("test@example.com")
                .build();

        User existingUser = User.builder()
                .id(1L)
                .name("기존유저")
                .email("test@example.com")
                .build();

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(existingUser));

        // When & Then
        assertThatThrownBy(() -> userService.createUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 이메일입니다.");
    }
}