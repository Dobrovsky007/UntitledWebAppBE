package com.webapp.Eventified.dto;

import java.util.List;

import com.webapp.Eventified.domain.Event;

import lombok.Data;

@Data
public class EventPoolDTO {
    private String title;
    private Integer sport;
    private String address;
    private String startTime;
    private Integer capacity;
    private Integer skillLevel;

    public EventPoolDTO(Event event) {
        this.title = event.getTitle();
        this.sport = event.getSport();
        this.address = event.getAddress();
        this.startTime = event.getStartTime().toString();
        this.capacity = event.getCapacity();
        this.skillLevel = event.getSkillLevel();
    }
}
