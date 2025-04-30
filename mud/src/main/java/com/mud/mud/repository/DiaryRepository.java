package com.mud.mud.repository;

import com.mud.mud.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DiaryRepository extends JpaRepository<Diary, String> {
    List<Diary> findByUser_UserId(String userId); // 유저별 일기 조회용
}