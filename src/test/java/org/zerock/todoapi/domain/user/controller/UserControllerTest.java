// src/test/java/org/zerock/todoapi/domain/user/controller/UserControllerTest.java
package org.zerock.todoapi.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.zerock.todoapi.domain.user.dto.CreateUserRequest;
import org.zerock.todoapi.domain.user.dto.UserResponse;
import org.zerock.todoapi.domain.user.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = {UserController.class, UserControllerTest.TestConfig.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("유저 생성 API 테스트")
    void createUser() throws Exception {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
                .name("테스트유저")
                .email("test@example.com")
                .build();

        UserResponse response = UserResponse.builder()
                .id(1L)
                .name("테스트유저")
                .email("test@example.com")
                .build();

        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("테스트유저"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("유효하지 않은 요청 - 이름 누락")
    void createUser_InvalidRequest_MissingName() throws Exception {
        // Given
        CreateUserRequest request = CreateUserRequest.builder()
                .email("test@example.com")
                .build();

        // When & Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Configuration
    static class TestConfig {

        @Bean
        @Primary
        public UserService userService() {
            return mock(UserService.class);
        }
    }
}