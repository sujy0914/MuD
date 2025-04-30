package com.mud.mud.service;

import com.mud.mud.entity.SavedSong;
import com.mud.mud.repository.SavedSongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SavedSongService {

    @Autowired
    private SavedSongRepository savedSongRepository;

    public SavedSong saveSong(SavedSong savedSong) {
        return savedSongRepository.save(savedSong);
    }

    public List<SavedSong> getSongsByUserId(String userId) {
        return savedSongRepository.findByUserId(userId);
    }

    public List<SavedSong> getSongsByDiaryId(String diaryId) {
        return savedSongRepository.findByDiaryId(diaryId);
    }

    public List<SavedSong> getAllSavedSongs() {
        return savedSongRepository.findAll();
    }
}
