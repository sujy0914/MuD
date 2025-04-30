package com.mud.mud.controller;

import com.mud.mud.entity.SavedSong;
import com.mud.mud.service.SavedSongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saved-songs")
public class SavedSongController {

    @Autowired
    private SavedSongService savedSongService;

    @PostMapping
    public SavedSong saveSong(@RequestBody SavedSong savedSong) {
        return savedSongService.saveSong(savedSong);
    }

    @GetMapping("/user/{userId}")
    public List<SavedSong> getSongsByUser(@PathVariable String userId) {
        return savedSongService.getSongsByUserId(userId);
    }

    @GetMapping("/diary/{diaryId}")
    public List<SavedSong> getSongsByDiary(@PathVariable String diaryId) {
        return savedSongService.getSongsByDiaryId(diaryId);
    }

    @GetMapping
    public List<SavedSong> getAllSongs() {
        return savedSongService.getAllSavedSongs();
    }
}
