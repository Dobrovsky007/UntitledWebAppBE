package com.webapp.Eventified.controller;


import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("my/past")
    public ResponseEntity<?> getAllMyPastEvents(Authentication authentication){
        String username = authentication.getName();

        if (eventService.getMyEventsPast(username).isEmpty()){
            return ResponseEntity.status(500).body("No past events found");
        } else {
            return ResponseEntity.ok(eventService.getMyEventsPast(username));
        }

    }

    @GetMapping("/filter/by/sport/{sport}")
    public ResponseEntity<?> getEventsBySport(@PathVariable Integer sport){
        if (eventService.getEventsBySport(sport).isEmpty()) {
            return ResponseEntity.status(500).body("No events for this sport found");
        } else {
            return ResponseEntity.ok(eventService.getEventsBySport(sport));
        }
    }

    @GetMapping("/filter/by/skillLevel/{skillLevel}")
    public ResponseEntity<?> getEventsBySkillLevel(@PathVariable Integer skillLevel){
        if(eventService.getEventsBySkillLevel(skillLevel).isEmpty()){
            return ResponseEntity.status(500).body("No events for this skill level found");
        } else{
            return ResponseEntity.ok(eventService.getEventsBySkillLevel(skillLevel));
        }
    }

    @GetMapping("/filter/by/startTimeAfter/{dateTime}")
    public ResponseEntity<?> getEventsByStartTimeAfter(@PathVariable String dateTime){
        
        LocalDateTime parseDateTime = LocalDateTime.parse(dateTime);

        if (eventService.getEventsByStartTimeAfter(parseDateTime).isEmpty()) {
            return ResponseEntity.status(500).body("No events found after this date");
        } else{
            return ResponseEntity.ok(getEventsByStartTimeAfter(dateTime));
        }
    }

    @GetMapping("/filter/by/startTimeBefore/{dateTime}")
    public ResponseEntity<?> getEventsByEndTimeBefore(@PathVariable String dateTime){
        LocalDateTime parseDateTime = LocalDateTime.parse(dateTime);

        if(eventService.getEventsByEndTimeBefore(parseDateTime).isEmpty()){
            return ResponseEntity.status(500).body("No events found before this date");
        } else{
            return ResponseEntity.ok(eventService.getEventsByEndTimeBefore(parseDateTime));
        }
    }

    @GetMapping("/filter/by/freeSlots/{freeSlots}")
    public ResponseEntity<?> getEventsByFreeSlots(@PathVariable Integer freeSlots){
        if(eventService.getEventsByFreeSlots(freeSlots).isEmpty()){
            return ResponseEntity.status(500).body("No events found with that number of free slots");
        } else{
            return ResponseEntity.ok(eventService.getEventsByFreeSlots(freeSlots));
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<?> getFilteredEvents(
            @RequestParam (required = false) List<Integer> sports,
            @RequestParam (required = false) List<Integer> skillLevels,
            @RequestParam (required = false) String startTimeAfter,
            @RequestParam (required = false) String endTimeBefore,
            @RequestParam (required = false) Integer freeSlots){

        try {
            LocalDateTime startTimeAfterParsed = startTimeAfter != null ? LocalDateTime.parse(startTimeAfter) : null;
            LocalDateTime endTimeBeforeParsed = endTimeBefore != null ? LocalDateTime.parse(endTimeBefore) : null;

            var filteredEvents = eventService.getFilteredEvents(sports, skillLevels, startTimeAfterParsed, endTimeBeforeParsed, freeSlots);

            if(filteredEvents.isEmpty()){
                return ResponseEntity.status(500).body("No events found with your criteria selected");
            } else{
                return ResponseEntity.ok(filteredEvents);
            }
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid filtered parameters" + e.getMessage());
        }
    }
}
