package com.mud.mud.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "saved_songs")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SavedSong {
    @Id
    @Column(name = "saved_id")
    private String savedId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "diary_id")
    private String diaryId;

    @Column(name = "spotify_id")
    private String spotifyId;
    private String title;
    private String artist;
    private String album;


}
