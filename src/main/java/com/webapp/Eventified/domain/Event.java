package com.webapp.Eventified.domain;

import java.math.BigDecimal;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(exclude = {"participants"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "occupied" , nullable = false)
    private Integer occupied;

    @Column(name = "sport", nullable = false)
    private Integer sport;

    @Column(name = "skill_level", nullable = false)
    private Integer skillLevel;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "latitude", nullable = false)
    private BigDecimal latitude;

    @Column(name = "longitude", nullable = false)
    private BigDecimal longitude;

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

    public Event(User organizer, String title, Integer sport, Integer skillLevel, String address, BigDecimal latitude, BigDecimal longitude, LocalDateTime startTime, LocalDateTime endTime, Integer capacity){
        this.organizer = organizer;
        this.title = title;
        this.sport = sport;
        this.skillLevel = skillLevel;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
        this.statusOfEvent = 0;
        this.createdAt = LocalDateTime.now();
    }
}
