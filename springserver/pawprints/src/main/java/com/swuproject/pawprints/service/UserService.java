package com.swuproject.pawprints.service;

import com.swuproject.pawprints.domain.User;
import com.swuproject.pawprints.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(User user) {
        Optional<User> existingUserById = userRepository.findByUserId(user.getUserId());
        if (existingUserById.isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }

        Optional<User> existingUserByEmail = userRepository.findByUserEmail(user.getUserEmail());
        if (existingUserByEmail.isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        userRepository.save(user);
    }

    public User loginUser(String userId, String userPw) {
        // 로그인 로직 구현
        return userRepository.findByUserId(userId)
                .filter(user -> user.getUserPw().equals(userPw))
                .orElse(null);
    }

    public boolean isUserIdAvailable(String userId) {
        return !userRepository.findByUserId(userId).isPresent();
    }

    public boolean isUserEmailAvailable(String userEmail) {
        return !userRepository.findByUserEmail(userEmail).isPresent();
    }
}