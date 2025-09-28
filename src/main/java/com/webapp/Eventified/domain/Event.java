package com.webapp.Eventified.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    @Column(name = "sport", nullable = false)
    private String sport;

    @Column(name = "skill_level", nullable = false)
    private Integer skillLevel;

    @Column(name = "adress", nullable = false)
    private String adress;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "status_of_event", nullable = false)
    private Integer statusOfEvent;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private Set<EventParticipant> participants = new HashSet<>();

    public Event(){}

    public Event(User organizer, String sport, Integer skillLevel, String adress, Double latitude, Double longitude, LocalDateTime startTime, LocalDateTime endTime, Integer capacity){
        this.organizer = organizer;
        this.sport = sport;
        this.skillLevel = skillLevel;
        this.adress = adress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
        this.statusOfEvent = 0;
        this.createdAt = LocalDateTime.now();
    }
}
