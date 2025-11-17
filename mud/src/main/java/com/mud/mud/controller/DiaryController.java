package com.mud.mud.controller;

import com.mud.mud.entity.Diary;
import com.mud.mud.service.DiaryService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/diary")
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    //1. 일기 작성
    @PostMapping("/write/{userId}")
    public ResponseEntity<Map<String, Object>> writeDiary(@RequestBody Diary diary, @PathVariable String userId) {
        Long diaryId = diaryService.writeDiary(diary, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("diaryId", diaryId);

        return ResponseEntity.ok(response);
    }

    // 2. 유저별 일기 목록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Diary>> getDiariesByUserId(@PathVariable String userId) {
        List<Diary> diaries = diaryService.getDiariesByUserId(userId);
        return ResponseEntity.ok(diaries);
    }

    // 3. 일기 수정
    @PutMapping("/{diaryId}")
    public ResponseEntity<String> updateDiary(@PathVariable String diaryId, @RequestBody Diary updatedDiary) {
        boolean updated = diaryService.updateDiary(diaryId, updatedDiary);
        if (updated) {
            return ResponseEntity.ok("일기 수정 완료");
        } else {
            return ResponseEntity.badRequest().body("일기 수정 실패 또는 해당 일기 없음");
        }
    }

    // 일기 삭제 (날짜 기준)
    @DeleteMapping("/{userId}/date")
    public ResponseEntity<String> deleteDiary(
            @PathVariable String userId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        boolean deleted = diaryService.deleteDiaryByUserIdAndDate(userId, date);
        if (deleted) {
            return ResponseEntity.ok("삭제 완료");
        } else {
            return ResponseEntity.badRequest().body("삭제할 일기가 없습니다.");
        }
    }

    @GetMapping("/{userId}/date")
    public ResponseEntity<List<Diary>> getDiaryByUserIdAndDate(
            @PathVariable String userId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<Diary> diaries = diaryService.getDiariesByUserIdAndDate(userId, date);
        if (diaries.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(diaries);
    }

    @GetMapping("/summary/{userId}")
    public ResponseEntity<Map<String, Object>> getDiarySummary(@PathVariable String userId) {
        Map<String, Object> summary = diaryService.getDiarySummary(userId);
        return ResponseEntity.ok(summary);
    }
}