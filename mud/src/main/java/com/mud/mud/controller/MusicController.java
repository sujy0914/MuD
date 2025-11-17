package com.mud.mud.controller;


import com.mud.mud.entity.TrackDto;
import com.mud.mud.service.MusicService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/music")
public class MusicController {
    private final MusicService musicService;

    public MusicController(MusicService musicService) {
        this.musicService = musicService;
    }

    @GetMapping("/recommend")
    public ResponseEntity<List<TrackDto>> recommendMusic(
            @RequestParam String weather,
            @RequestParam String mood,
            @RequestParam String time) {
        try {
            List<TrackDto> recommendedTracks = musicService.recommendTracks(weather, mood, time);
            return ResponseEntity.ok(recommendedTracks);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
