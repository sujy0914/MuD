package com.mud.mud.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name = "diary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Diary {

    @Id
    @Column(name = "diary_id")
    private String diaryId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String content;
    private String weather;
    private String mood;

    @Column(name = "time_of_day")
    private String timeOfDay;

    private LocalDateTime createdAt;

}
