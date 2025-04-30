package com.mud.mud.controller;

import com.mud.mud.entity.User;
import com.mud.mud.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // ✅ 회원가입 API
    @PostMapping
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        // 유효성 검사
        if (user.getUserId() == null || user.getUserId().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("아이디를 입력해주세요.");
        }
        if (user.getPassword() == null || user.getPassword().length() < 8) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호는 최소 8자 이상이어야 합니다.");
        }
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이름을 입력해주세요.");
        }

        try {
            userService.saveUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 실패: " + e.getMessage());
        }
    }

    // ✅ 사용자 정보 조회
    @GetMapping("/{userId}")
    public ResponseEntity<Optional<User>> getUser(@PathVariable String userId) {
        Optional<User> user = userService.getUserById(userId);
        return user.isPresent() ? ResponseEntity.ok(user) : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // ✅ 로그인 API
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user, HttpSession session) {
        User loginUser = userService.login(user.getUserId(), user.getPassword());

        if (loginUser != null) {
            // 세션에 사용자 정보 저장
            session.setAttribute("loginUser", loginUser);
            return ResponseEntity.ok().body(Map.of(
                    "message", "로그인 성공",
                    "userId", loginUser.getUserId(),
                    "username", loginUser.getUsername()
            ));
        } else {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "아이디 또는 비밀번호가 틀렸습니다."));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    // ✅ 아이디 중복 체크 (필요 시 주석 해제)
    /*
    @GetMapping("/check")
    public ResponseEntity<?> checkUserId(@RequestParam String userId) {
        boolean isAvailable = userService.checkUserIdAvailability(userId);
        return ResponseEntity.ok().body("{\"available\": " + isAvailable + "}");
    }
    */
}
