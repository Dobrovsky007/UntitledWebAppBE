package com.webapp.Eventified.controller;


import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.webapp.Eventified.dto.EventRequest;
import com.webapp.Eventified.service.EventService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/event")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createEvent(@RequestBody EventRequest request, Authentication authentication) {

        try {
            String username = authentication.getName();

            eventService.createEvent(
                    username,
                    request.getTitle(),
                    request.getSport(),
                    request.getAddress(),
                    request.getSkillLevel(),
                    request.getStartTime(),
                    request.getEndTime(),
                    request.getCapacity(),
                    request.getLatitude(),
                    request.getLongitude());

            return ResponseEntity.status(HttpStatus.CREATED).body("Event created successfully");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllEvents(){
        if (eventService.getAllEvents().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No events found");
        } else {
            return ResponseEntity.ok(eventService.getAllEvents());
        }
    }

    @GetMapping("/my/upcoming")
    public ResponseEntity<?> getAllMyUpcomingEvents(Authentication authentication){
        String username = authentication.getName();
        if (eventService.getMyEventsUpcoming(username).isEmpty()){
            return ResponseEntity.status(500).body("No upcoming events found");
        } else {
            return ResponseEntity.ok(eventService.getMyEventsUpcoming(username));
        }
    }
}
