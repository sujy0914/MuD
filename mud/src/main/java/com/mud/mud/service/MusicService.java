package com.mud.mud.service;

import com.mud.mud.LastFMApiClient;
import com.mud.mud.entity.TrackDto;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.*;

@Service
public class MusicService {
    private final LastFMApiClient lastFmApiClient;

    public MusicService(LastFMApiClient lastFmApiClient) {
        this.lastFmApiClient = lastFmApiClient;
    }

    public List<TrackDto> recommendTracks(String weather, String mood, String time) throws IOException {
        List<String> tags = getTags(weather, mood, time);
        Set<TrackDto> trackSet = new LinkedHashSet<>();

        for (String tag : tags) {
            JsonNode response = lastFmApiClient.getTopTracksByTag(tag, 10);
            JsonNode tracks = response.has("tracks") ? response.path("tracks").path("track")
                    : response.path("toptracks").path("track");

            System.out.println("Last.fm 응답 for tag '" + tag + "': " + response);

            if (tracks.isArray()) {
                for (JsonNode track : tracks) {
                    String name = track.path("name").asText("Unknown Title");
                    String artist = track.path("artist").path("name").asText("Unknown Artist");
                    String url = track.path("url").asText("#");

                    // image 배열에서 가장 큰 이미지 (extralarge) 찾기
                    String imageUrl = "";
                    JsonNode images = track.path("image");
                    for (JsonNode img : images) {
                        if ("extralarge".equals(img.path("size").asText())) {
                            imageUrl = img.path("#text").asText();
                            break;
                        }
                    }

                    trackSet.add(new TrackDto(name, artist, url, imageUrl));
                }

            } else {
                System.out.println("추천된 트랙이 없습니다. tag: " + tag);
            }
        }

        return new ArrayList<>(trackSet);
    }

    private List<String> getTags(String weather, String mood, String time) {
        Map<String, String> weatherTags = Map.of(
                "sunny", "pop",
                "cloudy", "ambient",
                "rainy", "chill",
                "snow", "holiday",
                "thunder", "rock"
        );

        Map<String, String> moodTags = Map.of(
                "happy", "happy",
                "soso", "calm",
                "sad", "sad",
                "lovely", "romantic",
                "angry", "aggressive",
                "think", "instrumental"
        );

        Map<String, String> timeTags = Map.of(
                "morning", "energetic",
                "afternoon", "chill",
                "evening", "relax",
                "night", "sleep"
        );

        List<String> tags = new ArrayList<>();
        String weatherTag = weatherTags.getOrDefault(weather.toLowerCase(), "pop");
        String moodTag = moodTags.getOrDefault(mood.toLowerCase(), "pop");
        String timeTag = timeTags.getOrDefault(time.toLowerCase(), "pop");

        // 로그 추가
        System.out.println("Weather tag: " + weatherTag);
        System.out.println("Mood tag: " + moodTag);
        System.out.println("Time tag: " + timeTag);

        tags.add(weatherTag);
        tags.add(moodTag);
        tags.add(timeTag);

        return tags;
    }
}
