package com.mud.mud.service;


import com.mud.mud.entity.Diary;
import com.mud.mud.entity.SavedSong;
import com.mud.mud.repository.SavedSongRepository;
import org.springframework.stereotype.Service;
import com.mud.mud.repository.DiaryRepository;
import com.mud.mud.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;
    private final SavedSongRepository savedSongRepository;

    public DiaryService(DiaryRepository diaryRepository, UserRepository userRepository, SavedSongRepository savedSongRepository) {
        this.diaryRepository = diaryRepository;
        this.userRepository = userRepository;
        this.savedSongRepository = savedSongRepository;
    }

    // 일기 저장
    public Long writeDiary(Diary diary, String userId) {
        // 사용자 ID 설정
        diary.setUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")));

        // ✅ 날짜를 date 필드에만 저장
        // diary.setDate()는 프론트에서 전달된 값이 @RequestBody에 포함되어 있어야 함
        Diary saved = diaryRepository.save(diary);
        return saved.getDiaryId();
    }

    @Transactional
    public boolean deleteDiaryByUserIdAndDate(String userId, LocalDate date) {
        List<Diary> diaries = diaryRepository.findByUser_UserIdAndDate(userId, date);

        if (diaries.isEmpty()) return false;

        diaryRepository.deleteByUser_UserIdAndDate(userId, date);
        return true;
    }

    public List<Diary> getDiariesByUserIdAndDate(String userId, LocalDate date) {
        return diaryRepository.findByUser_UserIdAndDate(userId, date);
    }

    //일기수정
    @Transactional
    public boolean updateDiary(String diaryId, Diary updatedDiary) {
        return diaryRepository.findById(diaryId).map(existingDiary -> {
            existingDiary.setTittle(updatedDiary.getTittle());
            existingDiary.setContent(updatedDiary.getContent());
            existingDiary.setMood(updatedDiary.getMood());
            existingDiary.setWeather(updatedDiary.getWeather());
            existingDiary.setTimeOfDay(updatedDiary.getTimeOfDay());
            existingDiary.setDate(updatedDiary.getDate());
            diaryRepository.save(existingDiary);
            return true;
        }).orElse(false);
    }


    // 유저별 일기 목록 조회
    public List<Diary> getDiariesByUserId(String userId) {
        return diaryRepository.findByUser_UserId(userId);
    }

    public Map<String, Object> getDiarySummary(String userId) {
        List<Diary> diaries = diaryRepository.findByUser_UserId(userId); // User 객체 안의 userId
        int diaryCount = diaries.size();

        // Top Mood
        Map<String, Long> moodCount = diaries.stream()
                .collect(Collectors.groupingBy(Diary::getMood, Collectors.counting()));

        String topMood = moodCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        // Diary Summary for Calendar
        List<Map<String, String>> diarySummaries = diaries.stream()
                .map(d -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("date", d.getDate().toString());
                    map.put("mood", d.getMood());
                    return map;
                })
                .collect(Collectors.toList());

        // Top Song
        List<SavedSong> songs = savedSongRepository.findByUserId(userId);

        String topSong = songs.stream()
                .collect(Collectors.groupingBy(song -> song.getTitle() + " - " + song.getArtist(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        Map<String, Object> summary = new HashMap<>();
        summary.put("diaryCount", diaryCount);
        summary.put("topMood", topMood);
        summary.put("topSong", topSong);
        summary.put("diaries", diarySummaries);

        return summary;
    }
}