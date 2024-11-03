package com.swuproject.pawprints.controller;

import com.swuproject.pawprints.domain.User;
import com.swuproject.pawprints.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            response.put("userId", user.getUserId());
            response.put("userEmail", user.getUserEmail());
            response.put("userName", user.getUserName());
            response.put("userNickname", user.getUserNickname());
            response.put("userPhone", user.getUserPhone());
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "로그인 실패");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);  // 로그인 실패의 경우 401 상태 코드 반환
        }
    }

    @GetMapping("/check-id")
    public ResponseEntity<Map<String, String>> checkUserId(@RequestParam String userId) {
        Map<String, String> response = new HashMap<>();
        if (userService.isUserIdAvailable(userId)) {
            response.put("message", "사용 가능한 아이디입니다.");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "이미 사용 중인 아이디입니다.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);  // 중복 아이디의 경우 409 상태 코드 반환
        }
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, String>> checkUserEmail(@RequestParam String userEmail) {
        Map<String, String> response = new HashMap<>();
        if (userService.isUserEmailAvailable(userEmail)) {
            response.put("message", "사용 가능한 이메일입니다.");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "이미 사용 중인 이메일입니다.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);  // 중복 이메일의 경우 409 상태 코드 반환
        }
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody Map<String, String> userUpdates) {
        try {
            userService.updateUser(userUpdates);
            return ResponseEntity.ok("사용자 정보가 성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("사용자 정보 업데이트 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/confirm-password")
    public ResponseEntity<String> confirmPassword(@RequestBody Map<String, String> passwordRequest) {
        String userId = passwordRequest.get("userId");
        String password = passwordRequest.get("userPw");

        User user = userService.loginUser(userId, password);
        if (user != null) {
            return ResponseEntity.ok("비밀번호 확인 성공");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 일치하지 않습니다.");
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        try {
            boolean deleted = userService.deleteUser(userId); // UserService에서 deleteUser 메서드를 호출
            if (deleted) {
                return ResponseEntity.ok("회원탈퇴가 완료되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원탈퇴 중 오류가 발생했습니다.");
        }
    }


}