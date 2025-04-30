package com.mud.mud.service;

import com.mud.mud.entity.User;
import com.mud.mud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ 사용자 등록 (암호화된 비밀번호 저장)
    public User saveUser(User user) {
        // 비밀번호 암호화 처리
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);
        return userRepository.save(user);
    }

    // ✅ 로그인 (암호화된 비밀번호 비교)
    public User login(String userId, String password) {
        Optional<User> userOptional = userRepository.findByUserId(userId);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user; // 로그인 성공
            }
        }
        return null; // 로그인 실패
    }

    // ✅ 사용자 아이디로 조회
    public Optional<User> getUserById(String userId) {
        return userRepository.findByUserId(userId);
    }
}
