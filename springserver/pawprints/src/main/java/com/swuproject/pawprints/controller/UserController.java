package com.swuproject.pawprints.controller;

import com.swuproject.pawprints.domain.User;
import com.swuproject.pawprints.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();
        try {
            userService.registerUser(user);
            response.put("message", "회원가입이 완료되었습니다.");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);  // 중복 아이디 또는 이메일의 경우 409 상태 코드 반환
        } catch (Exception e) {
            response.put("message", "회원가입에 실패했습니다: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);  // 기타 예외의 경우 500 상태 코드 반환
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<Map<String, String>> signin(@RequestBody Map<String, String> loginRequest) {
        String userId = loginRequest.get("userId");
        String userPw = loginRequest.get("userPw");
        User user = userService.loginUser(userId, userPw);
        Map<String, String> response = new HashMap<>();
        if (user != null) {
            response.put("message", "로그인 성공");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "로그인 실패");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);  // 로그인 실패의 경우 401 상태 코드 반환
        }
    }
}