package org.zerock.todoapi.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.todoapi.domain.user.dto.CreateUserRequest;
import org.zerock.todoapi.domain.user.dto.UserResponse;
import org.zerock.todoapi.domain.user.entity.User;
import org.zerock.todoapi.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserResponse createUser(CreateUserRequest request) {
        // 중복 이메일 검증
        validateEmailNotExists(request.getEmail());

        // 유저 생성
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();

        // 저장
        User savedUser = userRepository.save(user);

        // 응답 DTO 변환
        return UserResponse.from(savedUser);
    }

    private void validateEmailNotExists(String email) {
        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
                });
    }
}