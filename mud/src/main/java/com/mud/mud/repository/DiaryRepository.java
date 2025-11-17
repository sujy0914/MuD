package com.mud.mud.repository;

import com.mud.mud.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, String> {
    List<Diary> findByUser_UserId(String userId); // 유저별 일기 조회용

    List<Diary> findByUser_UserIdAndDate(String userId, LocalDate date);

    @Transactional
    void deleteByUser_UserIdAndDate(String userId, LocalDate date);}
