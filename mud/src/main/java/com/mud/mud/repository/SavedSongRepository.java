package com.mud.mud.repository;

import com.mud.mud.entity.SavedSong;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavedSongRepository extends JpaRepository<SavedSong, String> {
    List<SavedSong> findByUserId(String userId);
    List<SavedSong> findByDiaryId(Long diaryId);
}
