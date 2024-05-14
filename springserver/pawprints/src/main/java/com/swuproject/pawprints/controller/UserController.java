package com.swuproject.pawprints.controller;

import com.swuproject.pawprints.domain.User;
import com.swuproject.pawprints.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User user) {
        userService.registerUser(user);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @PostMapping("/signin")
    public ResponseEntity<String> signin(@RequestBody Map<String, String> loginRequest) {
        String userId = loginRequest.get("userId");
        String userPw = loginRequest.get("userPw");
        User user = userService.loginUser(userId, userPw);
        if (user != null) {
            return ResponseEntity.ok("로그인 성공");
        } else {
            return ResponseEntity.badRequest().body("로그인 실패");
        }
    }
}