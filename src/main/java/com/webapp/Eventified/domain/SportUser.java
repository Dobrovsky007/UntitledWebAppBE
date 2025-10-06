package com.webapp.Eventified.domain;

import jakarta.persistence.Entity;

import java.util.UUID;

import com.webapp.Eventified.domain.id.SportUserId;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user_sports")
@IdClass(SportUserId.class)
public class SportUser {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "sport")
    private Integer sport;

    @Column(nullable = false, name = "skill_level")
    private Integer skillLevel;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    public SportUser(){
    }

    public SportUser(UUID id, Integer sport, Integer skillLevel){
        this.userId = id;
        this.sport = sport;
        this.skillLevel = skillLevel;
    }
}
