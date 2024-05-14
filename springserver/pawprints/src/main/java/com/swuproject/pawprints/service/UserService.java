package com.swuproject.pawprints.service;

import com.swuproject.pawprints.domain.User;
import com.swuproject.pawprints.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void registerUser(User user) {
        // 회원 가입 로직 구현
        userRepository.save(user);
    }

    public User loginUser(String userId, String userPw) {
        // 로그인 로직 구현
        return userRepository.findByUserId(userId)
                .filter(user -> user.getUserPw().equals(userPw))
                .orElse(null);
    }
}