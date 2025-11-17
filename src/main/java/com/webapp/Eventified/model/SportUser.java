package com.webapp.Eventified.model;

import jakarta.persistence.Entity;

import java.util.UUID;

import com.webapp.Eventified.model.id.SportUserId;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing the relationship between a user and a sport.
 * Stores the user's skill level for each sport they participate in.
 * Uses a composite key of userId and sport.
 *
 * @author Eventified Team
 * @version 1.0
 */
@Getter
@Setter
@ToString(exclude = {"user"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "user_sports")
@IdClass(SportUserId.class)
public class SportUser {

    @EqualsAndHashCode.Include
    @Id
    @Column(name = "user_id")
    private UUID userId;

    @EqualsAndHashCode.Include
    @Id
    @Column(name = "sport")
    private Integer sport;

    @Column(nullable = false, name = "skill_level")
    private Integer skillLevel;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    /**
     * Default constructor for JPA.
     */
    public SportUser(){
    }

    /**
     * Constructs a new SportUser with the specified user ID, sport, and skill level.
     *
     * @param id the unique identifier of the user
     * @param sport the integer identifier of the sport type
     * @param skillLevel the user's skill level for this sport
     */
    public SportUser(UUID id, Integer sport, Integer skillLevel){
        this.userId = id;
        this.sport = sport;
        this.skillLevel = skillLevel;
    }
}
