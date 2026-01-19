package com.webapp.Eventified.controller.user;


import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.webapp.Eventified.dto.user.EventRequest;
import com.webapp.Eventified.service.EventService;


/**
 * REST controller for event-related endpoints.
 * Provides API endpoints for event management operations.
 *
 * @author Eventified Team
 * @version 1.0
 */
@RestController
@RequestMapping("/event")
public class EventController {

    private final EventService eventService;

    /**
     * Constructs a new EventController with the specified EventService.
     *
     * @param eventService the service layer for event-related operations
     */
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Creates a new event with the authenticated user as the organizer.
     * Validates the event details and ensures the user doesn't already have an event with the same title.
     *
     * @param request the event creation request containing all event details
     * @param authentication the Spring Security authentication object containing user credentials
     * @return ResponseEntity with success message if event is created, or error message if validation fails
     */
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

    @DeleteMapping("/delete/{eventId}")
    public ResponseEntity<?> deleteEvent(@PathVariable UUID eventId, Authentication authentication){

        String username = authentication.getName();

        try{
            eventService.cancelEvent(eventId, username);
            return ResponseEntity.ok("Event cancelled successfully");
        } 
        catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Retrieves all events in the system for public viewing.
     * Returns all events regardless of organizer, status, or timing.
     *
     * @return ResponseEntity containing all events as DTOs, or 404 if no events exist
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllEvents(){
        if (eventService.getAllEvents().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No events found");
        } else {
            return ResponseEntity.ok(eventService.getAllEvents());
        }
    }

    /**
     * Retrieves all upcoming events organized by the authenticated user.
     * Filters events where the start time is after the current timestamp.
     *
     * @param authentication the Spring Security authentication object containing user credentials
     * @return ResponseEntity containing upcoming events or empty array if none found
     */
    @GetMapping("/hosted/upcoming")
    public ResponseEntity<?> getAllMyUpcomingEvents(Authentication authentication){
        String username = authentication.getName();
        return ResponseEntity.ok(eventService.getHostedEventsUpcoming(username));
    }

    /**
     * Retrieves all past events organized by the authenticated user.
     * Filters events where the end time is before the current timestamp.
     *
     * @param authentication the Spring Security authentication object containing user credentials
     * @return ResponseEntity containing past events or empty array if none found
     */
    @GetMapping("/hosted/past")
    public ResponseEntity<?> getAllMyPastEvents(Authentication authentication){
        String username = authentication.getName();
        return ResponseEntity.ok(eventService.getHostedEventsPast(username));
    }

    /**
     * Retrieves all events filtered by a specific sport type.
     * Returns events that match the specified sport identifier.
     *
     * @param sport the integer identifier of the sport to filter by
     * @return ResponseEntity containing events for the specified sport or error if none found
     */
    @GetMapping("/filter/by/sport/{sport}")
    public ResponseEntity<?> getEventsBySport(@PathVariable Integer sport){
        if (eventService.getEventsBySport(sport).isEmpty()) {
            return ResponseEntity.status(500).body("No events for this sport found");
        } else {
            return ResponseEntity.ok(eventService.getEventsBySport(sport));
        }
    }

    /**
     * Retrieves all events filtered by a specific skill level requirement.
     * Returns events that match the specified skill level.
     *
     * @param skillLevel the integer identifier of the skill level to filter by
     * @return ResponseEntity containing events for the specified skill level or error if none found
     */
    @GetMapping("/filter/by/skillLevel/{skillLevel}")
    public ResponseEntity<?> getEventsBySkillLevel(@PathVariable Integer skillLevel){
        if(eventService.getEventsBySkillLevel(skillLevel).isEmpty()){
            return ResponseEntity.status(500).body("No events for this skill level found");
        } else{
            return ResponseEntity.ok(eventService.getEventsBySkillLevel(skillLevel));
        }
    }

    /**
     * Retrieves all events that start after a specified date and time.
     * Parses the datetime string parameter and filters events accordingly.
     *
     * @param dateTime the ISO datetime string after which events should start
     * @return ResponseEntity containing events starting after the specified time or error if none found
     */
    @GetMapping("/filter/by/startTimeAfter/{dateTime}")
    public ResponseEntity<?> getEventsByStartTimeAfter(@PathVariable String dateTime){
        
        LocalDateTime parseDateTime = LocalDateTime.parse(dateTime);

        if (eventService.getEventsByStartTimeAfter(parseDateTime).isEmpty()) {
            return ResponseEntity.status(500).body("No events found after this date");
        } else{
            return ResponseEntity.ok(getEventsByStartTimeAfter(dateTime));
        }
    }

    /**
     * Retrieves all events that end before a specified date and time.
     * Parses the datetime string parameter and filters events accordingly.
     *
     * @param dateTime the ISO datetime string before which events should end
     * @return ResponseEntity containing events ending before the specified time or error if none found
     */
    @GetMapping("/filter/by/startTimeBefore/{dateTime}")
    public ResponseEntity<?> getEventsByEndTimeBefore(@PathVariable String dateTime){
        LocalDateTime parseDateTime = LocalDateTime.parse(dateTime);

        if(eventService.getEventsByEndTimeBefore(parseDateTime).isEmpty()){
            return ResponseEntity.status(500).body("No events found before this date");
        } else{
            return ResponseEntity.ok(eventService.getEventsByEndTimeBefore(parseDateTime));
        }
    }

    /**
     * Retrieves all events that have at least the specified number of free spots available.
     * Calculates available capacity by comparing total capacity with occupied slots.
     *
     * @param freeSlots the minimum number of free spots required
     * @return ResponseEntity containing events with sufficient available capacity or error if none found
     */
    @GetMapping("/filter/by/freeSlots/{freeSlots}")
    public ResponseEntity<?> getEventsByFreeSlots(@PathVariable Integer freeSlots){
        if(eventService.getEventsByFreeSlots(freeSlots).isEmpty()){
            return ResponseEntity.status(500).body("No events found with that number of free slots");
        } else{
            return ResponseEntity.ok(eventService.getEventsByFreeSlots(freeSlots));
        }
    }

    /**
     * Retrieves events filtered by multiple optional criteria including sport, skill level, timing, and capacity.
     * All parameters are optional and will be ignored if not provided. Combines multiple filters for precise event discovery.
     *
     * @param sports optional list of sport IDs to filter by
     * @param skillLevels optional list of skill levels to filter by
     * @param startTimeAfter optional ISO datetime string for minimum start time
     * @param endTimeBefore optional ISO datetime string for maximum end time
     * @param freeSlots optional minimum number of free spots required
     * @return ResponseEntity containing filtered events or error message if none match criteria
     */
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

            return ResponseEntity.ok(filteredEvents);
        } catch (Exception e){
            System.err.println("Error in filter endpoint: " + e.getClass().getName() + " - " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid filter parameters: " + e.getClass().getName() + " - " + e.getMessage());
        }
    }

    @GetMapping("/attended/past")
    public ResponseEntity<?> getMyAttendedPastEvents(Authentication authentication){
        String username = authentication.getName();
        return ResponseEntity.ok(eventService.getMyAttendedPastEvents(username));
    }

    @GetMapping("/attended/upcoming")
    public ResponseEntity<?> getMyAttendedUpcomingEvents(Authentication authentication){
        String username = authentication.getName();
        return ResponseEntity.ok(eventService.getMyAttendedUpcomingEvents(username));
    }

    @GetMapping("/details/{eventId}")
    public ResponseEntity<?> getEventDetails(@PathVariable UUID eventId){

        if (eventService.getEventDetails(eventId).equals(null)) {
            return ResponseEntity.status(500).body("Event not found");
        } else{
            return ResponseEntity.ok(eventService.getEventDetails(eventId));
        }
    }
}
