package org.zerock.todoapi.domain.user.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class UserTest {

    @Test
    @DisplayName("유저 생성 테스트")
    void createUser() {
        // Given
        String name = "테스트유저";
        String email = "test@example.com";

        // When
        User user = User.builder()
                .name(name)
                .email(email)
                .build();

        // Then
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getEmail()).isEqualTo(email);
    }
}