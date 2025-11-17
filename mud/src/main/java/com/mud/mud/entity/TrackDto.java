package com.mud.mud.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrackDto {
    private String name;
    private String artist;
    private String url;
    private String imageUrl; // 추가

    public TrackDto(String name, String artist, String url, String imageUrl) {
        this.name = name;
        this.artist = artist;
        this.url = url;
        this.imageUrl = imageUrl;
    }
}
