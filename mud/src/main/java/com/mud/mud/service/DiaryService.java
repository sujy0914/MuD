package com.mud.mud.service;


import com.mud.mud.entity.Diary;
import com.mud.mud.entity.User;
import com.mud.mud.repository.DiaryRepository;
import com.mud.mud.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    public DiaryService(DiaryRepository diaryRepository, UserRepository userRepository) {
        this.diaryRepository = diaryRepository;
        this.userRepository = userRepository;
    }

    public String writeDiary(Diary diary, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));

        diary.setUser(user);
        diary.setCreatedAt(LocalDateTime.now());

        return diaryRepository.save(diary).getDiaryId();
    }


    // 일기 저장
    public Diary saveDiary(Diary diary) {
        System.out.println(">>> Saving Diary: " + diary);
        return diaryRepository.save(diary);
    }

    // 유저별 일기 목록 조회
    public List<Diary> getDiariesByUserId(String userId) {
        return diaryRepository.findByUser_UserId(userId);

    }
}