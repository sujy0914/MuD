package com.mud.mud.repository;

import com.mud.mud.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByUserId(String userId);

    Optional<User> findByUserId(String userId);

    // 회원 조회 - 모든 회원 정보 조회
    List<User> findAll();

    // 회원 수정 - 전화번호만 변경 가능
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.phone = :phone WHERE u.userId = :userId")
    int updatePhone(@Param("userId") String userId, @Param("phone") String phone);  // String으로 수정

    // 회원 탈퇴 - 로그인된 회원의 아이디를 탈퇴 시킨다.
    @Modifying
    @Transactional
    @Query("DELETE FROM User u WHERE u.userId = :userId")
    void deleteByUserId(String userId);

}