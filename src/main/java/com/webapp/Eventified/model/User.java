package com.webapp.Eventified.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entity representing a user in the Eventified application.
 * Manages user authentication, profile information, and relationships with
 * events and sports.
 *
 * @author Eventified Team
 * @version 1.0
 */
@Getter
@Setter
@ToString(exclude = { "passwordHash", "sports", "organizedEvents", "eventParticipations" })
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "users")
public class User {

    @EqualsAndHashCode.Include
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SecureTokenEmail> tokens = new HashSet<>();

    @OneToMany(mappedBy = "requester", cascade = CascadeType.ALL)
    private Set<Friendship> sentFriendRequests = new HashSet<>();

    @OneToMany(mappedBy = "addressee", cascade = CascadeType.ALL)
    private Set<Friendship> receivedFriendRequests = new HashSet<>();

    /**
     * Default constructor for JPA.
     */
    public User() {
    }

    /**
     * Constructs a new User with the specified credentials.
     * Initializes default values for verification status, timestamps, and scores.
     *
     * @param username     the unique username for the user
     * @param email        the unique email address for the user
     * @param passwordHash the hashed password for authentication
     */
    public User(String username, String email, String passwordHash) {
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
