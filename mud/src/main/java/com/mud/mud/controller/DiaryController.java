package com.mud.mud.controller;

import com.mud.mud.entity.Diary;
import com.mud.mud.service.DiaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/diaries")
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    //1. 일기 작성
    @PostMapping("/write/{userId}")
    public ResponseEntity<String> writeDiary(@RequestBody Diary diary, @PathVariable String userId) {
        String diaryId = diaryService.writeDiary(diary, userId);
        return ResponseEntity.ok(diaryId);
    }

    // 2. 유저별 일기 목록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Diary>> getDiariesByUserId(@PathVariable String userId) {
        List<Diary> diaries = diaryService.getDiariesByUserId(userId);
        return ResponseEntity.ok(diaries);
    }
}