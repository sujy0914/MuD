package com.mud.mud.service;

import com.mud.mud.entity.SavedSong;
import com.mud.mud.repository.SavedSongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SavedSongService {

    @Autowired
    private final SavedSongRepository savedSongRepository;

    public SavedSongService(SavedSongRepository savedSongRepository) {
        this.savedSongRepository = savedSongRepository;
    }

    public SavedSong saveSong(SavedSong savedSong) {
        return savedSongRepository.save(savedSong);  // 모든 필드 저장
    }

    public List<SavedSong> getSongsByUserId(String userId) {
        return savedSongRepository.findByUserId(userId);
    }

    public List<SavedSong> getAllSavedSongs() {
        return savedSongRepository.findAll();
    }}
