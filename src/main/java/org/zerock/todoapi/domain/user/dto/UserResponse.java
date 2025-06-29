package org.zerock.todoapi.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.zerock.todoapi.domain.user.entity.User;

@Getter
@Builder
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String name;
    private String email;

    // 정적 팩토리 메서드
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}