package com.webapp.Eventified.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.webapp.Eventified.domain.Event;
import com.webapp.Eventified.domain.User;
import com.webapp.Eventified.repository.EventRepository;
import com.webapp.Eventified.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public Event createEvent(UUID userId, String title, Integer sport, String address, Integer skillLevel, LocalDateTime startTime, LocalDateTime endTime, Integer capacity, Double latitude, Double longitude) {

        if (eventRepository.findByTitleAndOrganizer_Id(title, userId).isPresent()){ 
            throw new IllegalArgumentException("Event with the same title already exists for this user.");
        }

        User organizer = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Event event = new Event(organizer, title, sport, skillLevel, address, latitude, longitude, startTime, endTime, capacity);

        return eventRepository.save(event);

    }
    
}
