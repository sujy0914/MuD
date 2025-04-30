package com.mud.mud.controller;

import com.mud.mud.entity.User;
import com.mud.mud.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // ✅ 회원가입 API
    @PostMapping
    public ResponseEntity<String> registerUser(@RequestBody User user,
                                               @RequestParam(required = false) Boolean isDuplicateChecked) {
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

        if (isDuplicateChecked == null || !isDuplicateChecked)
            return ResponseEntity.badRequest().body("아이디 중복 확인을 해주세요.");

        try {
            // 회원가입 성공 후 리다이렉트
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/public/login?registered=true")
                    .build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 실패: " + e.getMessage());
        }
    }

    @GetMapping("/check-duplicate")
    public ResponseEntity<Map<String, Boolean>> checkDuplicate(@RequestParam String userId) {
        boolean isDuplicate = userService.isUserIdDuplicate(userId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isDuplicate", isDuplicate);
        return ResponseEntity.ok(response);
    }

    // 로그인 페이지를 GET 요청으로 처리
    @GetMapping("/login")
    public String showLoginPage(HttpSession session, Model model) {
        Object isLoggedIn = session.getAttribute("isLoggedIn");
        model.addAttribute("isLoggedIn", isLoggedIn != null && (Boolean) isLoggedIn);
        return "screens/login";
    }

    // 로그인 폼 데이터 처리 (POST 요청)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest,
                                   HttpServletRequest request) {
        String userId = loginRequest.get("userId");
        String password = loginRequest.get("password");

        Optional<User> authenticatedUser = userService.authenticate(userId, password);

        if (authenticatedUser.isPresent()) {
            HttpSession session = request.getSession(true);
            User user = authenticatedUser.get();
            session.setAttribute("user", user);
            session.setAttribute("isLoggedIn", true);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "로그인 성공");
            response.put("userId", user.getUserId());
            response.put("username", user.getUsername());

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("아이디 또는 비밀번호가 잘못되었습니다.");
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // 기존 세션이 있으면 가져오기
        if (session != null) {
            session.invalidate(); // 세션을 무효화
        }
        return "redirect:/"; // 로그아웃 후 메인 페이지로 리디렉션
    }

    // 마이페이지
    @GetMapping("/mypage")
    public String showMyPage(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("user");

        if (loggedInUser == null) {
            return "redirect:/login.html?error=notLoggedIn";  // 로그인되지 않은 경우 login.html로 리다이렉트
        }

        model.addAttribute("user", loggedInUser);
        model.addAttribute("userId", loggedInUser.getUserId());
        return "screens/mypage.html";
    }

    // 전화번호 수정
    @PostMapping("/update-phone")
    public String updatePhone(@RequestParam String newPhone, HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("user");

        if (loggedInUser == null) {
            return "redirect:/login.html?error=notLoggedIn";  // 로그인되지 않은 경우 login.html로 리다이렉트
        }

        // 전화번호 수정 로직
        userService.updatePhone(loggedInUser.getUserId(), newPhone); // 수정된 메서드 사용
        return "redirect:/mypage.html";  // 성공적으로 전화번호 수정 후 마이페이지로 리다이렉트
    }

    // 회원 탈퇴
    @PostMapping("/delete")
    public String deleteAccount(HttpSession session, Model model) {
        String userId = ((User) session.getAttribute("user")).getUserId();  // 변경된 부분

        String result = userService.deleteAccount(userId);

        if ("회원 탈퇴가 완료되었습니다.".equals(result)) {
            session.invalidate();
            return "redirect:/";
        } else {
            model.addAttribute("message", result);
            return "error";
        }
    }

    // 모든 회원 조회
    @GetMapping("/")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
