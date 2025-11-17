package com.mud.mud.controller;

import com.mud.mud.entity.SavedSong;
import com.mud.mud.service.SavedSongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saved")
public class SavedSongController {

    @Autowired
    private SavedSongService savedSongService;

    // 트랙 저장 메서드
    @PostMapping
    public SavedSong saveSong(@RequestBody SavedSong savedSong) {
        // artist가 없으면 빈 문자열 처리
        if (savedSong.getArtist() == null) {
            savedSong.setArtist(""); // artist 빈 값 처리
        }

        // SavedSong 엔티티에 저장된 정보를 저장소에 저장
        return savedSongService.saveSong(savedSong);
    }

    // userId로 저장된 노래 리스트 가져오기
    @GetMapping("/user/{userId}")
    public List<SavedSong> getSongsByUser(@PathVariable String userId) {
        return savedSongService.getSongsByUserId(userId);
    }

    // 모든 저장된 노래 리스트 가져오기
    @GetMapping
    public List<SavedSong> getAllSongs() {
        return savedSongService.getAllSavedSongs();
    }
}
