package com.mud.mud.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "saved_songs")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SavedSong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 숫자 자동 증가
    @Column(name = "saved_id")
    private int savedId;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "diary_id")
    private Long diaryId;

    private String title;
    private String artist;

    private LocalDate date;

    private String url;

}
