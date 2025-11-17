package com.mud.mud.service;

import com.mud.mud.entity.Find;
import com.mud.mud.entity.User;
import com.mud.mud.repository.FindRepository;
import com.mud.mud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FindRepository findRepository;

    @Autowired
    private JavaMailSender mailSender;

    // 회원 저장
    public User saveUser(User user) {
        // 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        return userRepository.save(user);
    }

    // 로그인 인증
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

    // 아이디 중복 확인
    public boolean isUserIdDuplicate(String userId) {
        return userRepository.existsByUserId(userId);
    }

    // 이름+이메일 일치하는 유저 찾아서 아이디 이메일로 보내기
    public boolean sendIdByEmail(String name, String email) {
        User user = userRepository.findByUsernameAndEmail(name, email);
        if (user == null) return false;

        String subject = "아이디 안내 메일";
        String text = String.format("안녕하세요 %s님,\n\n회원님의 아이디는 '%s' 입니다.\n\n감사합니다.", name, user.getUserId());

        return sendEmail(email, subject, text);
    }

    // 이름+아이디+이메일 일치하는 유저 찾아서 임시비밀번호 생성 + 이메일 발송 + DB 비밀번호 변경
    public boolean sendTemporaryPassword(String name, String userId, String email) {
        User user = userRepository.findByUserIdAndUsernameAndEmail(userId, name, email);
        if (user == null) return false;

        // 임시 비밀번호 생성 (랜덤 8자리)
        String tempPassword = generateRandomPassword(8);

        // 비밀번호 암호화 후 저장
        user.setPassword(passwordEncoder.encode(tempPassword));
        userRepository.save(user);

        String subject = "임시 비밀번호 안내 메일";
        String text = String.format(
                "안녕하세요 %s님,\n\n임시 비밀번호는 '%s' 입니다.\n로그인 후 반드시 마이페이지에서 비밀번호를 변경해주세요.\n\n감사합니다.",
                name, tempPassword);

        return sendEmail(email, subject, text);
    }

    // 이메일 발송 공통 메서드
    private boolean sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 임시 비밀번호 생성 메서드 예시 (랜덤 알파벳 + 숫자)
    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    // 비밀번호 업데이트
    @Transactional
    public void updatePassword(String userId, String newPassword) {
        Optional<User> userOptional = userRepository.findByUserId(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String encryptedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encryptedPassword);
            userRepository.save(user);
        } else {
            throw new IllegalStateException("유저를 찾을 수 없습니다.");
        }
    }

    // 비밀번호 인증
    public boolean authenticatePassword(String userId, String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("비밀번호가 비어있거나 null 입니다.");
        }

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("아이디를 찾을 수 없습니다."));

        return passwordEncoder.matches(rawPassword, user.getPassword());
    }


    // 전화번호 업데이트
    @Transactional
    public void updateEmail(String userId, String newEmail) {
        Optional<User> userOptional = userRepository.findByUserId(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setEmail(newEmail);
            userRepository.save(user);
        } else {
            throw new IllegalStateException("유저를 찾을 수 없습니다.");
        }
    }

    // 회원 탈퇴
    @Transactional
    public void deleteAccount(String userId) {
        userRepository.deleteByUserId(userId);
    }

    // 모든 회원 조회
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
