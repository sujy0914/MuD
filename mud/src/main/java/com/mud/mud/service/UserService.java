package com.mud.mud.service;

import com.mud.mud.entity.User;
import com.mud.mud.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 회원가입 처리
    public void register(User user) {
        user.setPassword(hashPassword(user.getPassword()));
        userRepository.save(user);
    }

    // 비밀번호를 SHA-256으로 해시 처리
    private String hashPassword(String password) {
        // 비밀번호 앞뒤 공백 제거
        String trimmedPassword = password.trim();

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(trimmedPassword.getBytes());  // 공백 제거 후 해시 처리
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("비밀번호 해시 처리 중 오류가 발생했습니다.", e);
        }
    }

    // ✅ 사용자 등록 (암호화된 비밀번호 저장)
    public User saveUser(User user) {
        // 비밀번호 암호화 처리
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        return userRepository.save(user);
    }

    // ✅ 로그인 (암호화된 비밀번호 비교)
    public Map<String, Object> login(String userId, String password) {
        Map<String, Object> response = new HashMap<>();

        Optional<User> userOptional = userRepository.findByUserId(userId);

        if (userOptional.isEmpty()) {
            response.put("success", false);
            response.put("message", "존재하지 않는 아이디입니다.");
            return response;
        }

        User user = userOptional.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            response.put("success", false);
            response.put("message", "비밀번호가 올바르지 않습니다.");
            return response;
        }

        // 로그인 성공
        response.put("success", true);
        response.put("user", user); // 또는 필요한 값만 추출
        return response;
    }

    // 로그인 로직
    public Optional<User> authenticate(String userId, String password) {
        Optional<User> userOptional = userRepository.findByUserId(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    // 기존 사용자 중복 확인
    public boolean isUserIdDuplicate(String userId) {
        return userRepository.existsByUserId(userId);
    }

    // 로그아웃 처리
    public String logout() {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "로그아웃되었습니다.";
    }

    // 전화번호 변경 처리
    @Transactional
    public String changePhone(String userId, String newPhone) { // String으로 수정
        int rowsUpdated = userRepository.updatePhone(userId, newPhone);  // String으로 수정
        if (rowsUpdated > 0) {
            return "전화번호가 성공적으로 변경되었습니다.";
        } else {
            throw new IllegalStateException("전화번호 변경에 실패했습니다. 유저를 찾을 수 없습니다.");
        }
    }

    // 회원 탈퇴 처리 (대여 중인 도서가 없을 때만 탈퇴 가능)
    @Transactional
    public String deleteAccount(String userId) {
        // 사용자 조회
        Optional<User> user = userRepository.findByUserId(userId);

        // 회원 삭제
        userRepository.deleteByUserId(userId);
        return "회원 탈퇴가 완료되었습니다.";
    }

    // 모든 회원 조회
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 관리자가 회원을 삭제하는 메서드
    public String removeUserByAdmin(String id) {
        userRepository.deleteById(id);
        return "회원이 삭제되었습니다.";
    }

    // 마이페이지 정보 가져오기
    public User getLoggedInUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            return (User) session.getAttribute("user");
        }
        return null; // 로그인되지 않은 경우

    }

    // ✅ 사용자 아이디로 조회
    public Optional<User> getUserById(String userId) {
        return userRepository.findByUserId(userId);
    }

    // 전화번호 업데이트 처리 메서드 수정
    @Transactional
    public void updatePhone(String userId, String newPhone) {
        int rowsUpdated = userRepository.updatePhone(userId, newPhone);  // String으로 처리
        if (rowsUpdated <= 0) {
            throw new IllegalStateException("전화번호 변경에 실패했습니다.");
        }
    }
}
