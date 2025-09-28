package com.webapp.Eventified.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false, name = "username")
    private String username;

    @Column(unique = true, nullable = false, name = "email")
    private String email;

    @Column(nullable = false, name = "password_hash")
    private String passwordHash;

    @Column(nullable = false, name = "role")
    private boolean isAdmin = false;

    @Column(nullable = false, name = "enabled")
    private boolean isVerified;

    @Column(nullable = false, name = "created_at")
    private Instant createdAt;

    @Column(nullable = false, name = "trust_score")
    private int trustScore;

    @Column(nullable = false, name = "number_of_reviews")
    private int numberOfReviews;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<SportUser> sports = new HashSet<>();

    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL)
    private Set<Event> organizedEvents = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<EventParticipant> eventParticipations = new HashSet<>();

    public User(){}

    public User(String username, String email, String passwordHash){
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.isVerified = false;
        this.createdAt = Instant.now();
        this.isAdmin = false;
        this.trustScore = 0;
        this.numberOfReviews = 0;
    }
}
