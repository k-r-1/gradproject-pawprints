package com.swuproject.pawprints.repository;

import com.swuproject.pawprints.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserId(String userId);
    Optional<User> findByUserEmail(String userEmail);  // 이메일로 사용자 조회 메서드 추가
}