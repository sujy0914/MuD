package com.mud.mud.controller;

import com.mud.mud.entity.UpdatePasswordAndEmailRequest;
import com.mud.mud.entity.User;
import com.mud.mud.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 회원가입 API
    @PostMapping("/register")
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

        if (userService.isUserIdDuplicate(user.getUserId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 사용 중인 아이디입니다.");
        }

        try {
            userService.saveUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 실패: " + e.getMessage());
        }
    }

    // 아이디 중복 확인
    @GetMapping("/check-duplicate")
    public ResponseEntity<Map<String, Boolean>> checkDuplicate(@RequestParam String userId) {
        boolean isDuplicate = userService.isUserIdDuplicate(userId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isDuplicate", isDuplicate);
        return ResponseEntity.ok(response);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest,
                                   HttpServletRequest request) {
        String userId = loginRequest.get("userId");
        String password = loginRequest.get("password");

        Optional<User> authenticatedUser = userService.authenticate(userId, password);

        if (authenticatedUser.isPresent()) {
            HttpSession session = request.getSession(true);  // 새로운 세션 생성 또는 기존 세션 사용
            User user = authenticatedUser.get();
            session.setAttribute("user", user);  // 세션에 유저 정보 저장
            session.setAttribute("isLoggedIn", true);  // 로그인 상태 플래그 설정

            Map<String, Object> response = new HashMap<>();
            response.put("message", "로그인 성공");
            response.put("userId", user.getUserId());
            response.put("username", user.getUsername());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 또는 비밀번호가 잘못되었습니다.");
        }
    }

    // 아이디 찾기 - 이름 + 이메일로 조회, 이메일로 아이디 발송
    @PostMapping("/find-id")
    public ResponseEntity<?> findId(@RequestBody Map<String, String> req) {
        String name = req.get("name");
        String email = req.get("email");

        if (name == null || email == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "이름과 이메일을 모두 입력해주세요."));
        }

        boolean sent = userService.sendIdByEmail(name, email);

        if (sent) {
            return ResponseEntity.ok(Map.of("success", true));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", "정보가 일치하지 않습니다."));
        }
    }

    // 비밀번호 찾기 - 이름 + 아이디 + 이메일로 조회, 이메일로 임시비밀번호 발송
    @PostMapping("/find-password")
    public ResponseEntity<?> findPassword(@RequestBody Map<String, String> req) {
        String name = req.get("name");
        String userId = req.get("userId");
        String email = req.get("email");

        if (name == null || userId == null || email == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "모든 정보를 입력해주세요."));
        }

        boolean sent = userService.sendTemporaryPassword(name, userId, email);

        if (sent) {
            return ResponseEntity.ok(Map.of("success", true));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("success", false, "message", "정보가 일치하지 않습니다."));
        }
    }

    // 로그아웃
    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);  // 기존 세션이 있으면 가져오기
        if (session != null) {
            session.invalidate();  // 세션 무효화
        }
        return ResponseEntity.ok("로그아웃 되었습니다.");
    }

    // 마이페이지
    @GetMapping("/mypage")
    public ResponseEntity<?> showMyPage(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user");

        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("userId", loggedInUser.getUserId());
        response.put("username", loggedInUser.getUsername());
        response.put("email", loggedInUser.getEmail());

        return ResponseEntity.ok(response);
    }

    // 로그인 상태 확인 API
    @GetMapping("/check-login")
    public ResponseEntity<Map<String, Object>> checkLoginStatus(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        // 세션에서 로그인 상태 확인
        Boolean isLoggedIn = (Boolean) session.getAttribute("isLoggedIn");

        if (isLoggedIn != null && isLoggedIn) {
            // 로그인 상태일 경우
            User loggedInUser = (User) session.getAttribute("user");
            response.put("isLoggedIn", true);
            response.put("userId", loggedInUser.getUserId());
            response.put("username", loggedInUser.getUsername());
        } else {
            // 로그인되지 않은 상태일 경우
            response.put("isLoggedIn", false);
        }

        return ResponseEntity.ok(response);
    }

    // 비밀번호 인증만 하는 API
    @PostMapping("/check-password")
    public ResponseEntity<String> checkPassword(@RequestBody Map<String, String> request, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user");

        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        String rawPassword = request.get("rawPassword");

        boolean isPasswordCorrect = userService.authenticatePassword(loggedInUser.getUserId(), rawPassword);

        if (isPasswordCorrect) {
            return ResponseEntity.ok("비밀번호 인증 성공");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 일치하지 않습니다.");
        }
    }


    // 수정
    @PostMapping("/update-password-email")
    public ResponseEntity<String> updatePasswordAndPhone(@RequestBody UpdatePasswordAndEmailRequest request, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user");

        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            userService.updatePassword(loggedInUser.getUserId(), request.getNewPassword());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("비밀번호 변경 실패: " + e.getMessage());
        }

        try {
            userService.updateEmail(loggedInUser.getUserId(), request.getNewEmail());
            return ResponseEntity.ok("변경되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이메일 변경 실패: " + e.getMessage());
        }
    }



    // 회원 탈퇴
    @PostMapping("/delete")
    public ResponseEntity<String> deleteAccount(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("user");

        if (loggedInUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            userService.deleteAccount(loggedInUser.getUserId());
            session.invalidate();
            return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 탈퇴 실패: " + e.getMessage());
        }
    }

    // 모든 회원 조회
    @GetMapping("/")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
}
