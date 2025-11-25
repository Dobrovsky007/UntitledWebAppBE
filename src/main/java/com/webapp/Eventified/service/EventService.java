package com.webapp.Eventified.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.webapp.Eventified.dto.user.EventDetailsDTO;
import com.webapp.Eventified.dto.user.EventParticipantDTO;
import com.webapp.Eventified.dto.user.EventPoolDTO;
import com.webapp.Eventified.dto.user.EventUpdateRequest;
import com.webapp.Eventified.model.Event;
import com.webapp.Eventified.model.User;
import com.webapp.Eventified.repository.EventParticipantRepository;
import com.webapp.Eventified.repository.EventRepository;
import com.webapp.Eventified.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service class for managing event-related operations in the Eventified
 * application.
 * Provides functionality for creating, retrieving, and managing events and
 * event participants.
 *
 * @author Eventified Team
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class EventService {

        private final EventRepository eventRepository;
        private final UserRepository userRepository;
        private final EventParticipantRepository eventParticipantRepository;

        @Autowired
        private final NotificationService notificationService;

        /**
         * Creates a new event in the system with the specified details.
         * Validates that the organizer exists and doesn't already have an event with
         * the same title.
         * The event is saved to the database and an EventParticipant record is
         * automatically created
         * for the organizer with role 0 (organizer).
         *
         * @param username   the username of the event organizer
         * @param title      the title of the event (must be unique per organizer)
         * @param sport      the integer identifier of the sport type
         * @param address    the physical address where the event will take place
         * @param skillLevel the required skill level for participants
         * @param startTime  the date and time when the event starts
         * @param endTime    the date and time when the event ends
         * @param capacity   the maximum number of participants allowed
         * @param latitude   the latitude coordinate of the event location
         * @param longitude  the longitude coordinate of the event location
         * @return Event the created and saved event entity
         * @throws IllegalArgumentException if the user is not found or an event with
         *                                  the same title already exists
         */
        public Event createEvent(String username, String title, Integer sport, String address, Integer skillLevel, LocalDateTime startTime, LocalDateTime endTime, Integer capacity, BigDecimal latitude, BigDecimal longitude) {

                User organizer = userRepository.findByUsername(username)
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                if (eventRepository.findByTitleAndOrganizer(title, organizer).isPresent()) {
                        throw new IllegalArgumentException("Event with the same title already exists for this user.");
                }

                Event event = new Event(organizer, title, sport, skillLevel, address, latitude, longitude, startTime,
                                endTime,
                                capacity);

                eventRepository.save(event);

                notificationService.notifyUsersOfNewEvent(event);

                return event;
        }

        /**
         * Retrieves all events in the system and converts them to DTOs for API
         * response.
         * This method provides a complete list of events regardless of status or
         * timing.
         *
         * @return List containing all events in the system as DTO objects
         */
        public List<EventPoolDTO> getAllEvents() {
                List<Event> events = eventRepository.findAll();
                List<EventPoolDTO> eventsDTO = events.stream().map(EventPoolDTO::new).toList();
                return eventsDTO;
        }

        /**
         * Retrieves all upcoming events organized by a specific user.
         * Filters events where the start time is after the current timestamp.
         *
         * @param username the username of the event organizer
         * @return List containing upcoming events organized by the user
         * @throws IllegalArgumentException if the user with the specified username is
         *                                  not found
         */
        public List<EventPoolDTO> getHostedEventsUpcoming(String username) {
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                List<Event> events = eventRepository.findByOrganizer(user);
                List<EventPoolDTO> upcomingEventsDTO = events.stream()
                                .filter(event -> LocalDateTime.now().isBefore(event.getStartTime()))
                                .map(EventPoolDTO::new)
                                .toList();
                return upcomingEventsDTO;
        }

        /**
         * Retrieves all past events organized by a specific user.
         * Filters events where the end time is before the current timestamp.
         *
         * @param username the username of the event organizer
         * @return List containing past events organized by the user
         * @throws IllegalArgumentException if the user with the specified username is
         *                                  not found
         */
        public List<EventPoolDTO> getHostedEventsPast(String username) {
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                List<Event> events = eventRepository.findByOrganizer(user);

                List<EventPoolDTO> pastEventsDTO = events.stream()
                                .filter(event -> LocalDateTime.now().isAfter(event.getEndTime()))
                                .map(EventPoolDTO::new)
                                .toList();

                return pastEventsDTO;
        }

        /**
         * Retrieves all events filtered by a specific sport type.
         * Returns events that match the specified sport identifier.
         *
         * @param sport the integer identifier of the sport to filter by
         * @return List containing events for the specified sport
         */
        public List<EventPoolDTO> getEventsBySport(Integer sport) {
                List<Event> events = eventRepository.findBySport(sport);

                List<EventPoolDTO> eventsBySport = events.stream()
                                .filter(event -> event.getSport() == sport)
                                .map(EventPoolDTO::new)
                                .toList();

                return eventsBySport;
        }

        /**
         * Retrieves all events filtered by a specific skill level requirement.
         * Returns events that match the specified skill level.
         *
         * @param skillLevel the integer identifier of the skill level to filter by
         * @return List containing events for the specified skill level
         */
        public List<EventPoolDTO> getEventsBySkillLevel(Integer skillLevel) {
                List<Event> events = eventRepository.findBySkillLevel(skillLevel);

                List<EventPoolDTO> eventsBySkillLevel = events.stream()
                                .filter(event -> event.getSkillLevel() == skillLevel)
                                .map(EventPoolDTO::new)
                                .toList();

                return eventsBySkillLevel;
        }

        /**
         * Retrieves all events that start after a specified date and time.
         * Useful for finding events happening from a certain point in the future.
         *
         * @param dateTime the LocalDateTime after which events should start
         * @return List containing events starting after the specified time
         */
        public List<EventPoolDTO> getEventsByStartTimeAfter(LocalDateTime dateTime) {
                List<Event> events = eventRepository.findByStartTimeAfter(dateTime);

                List<EventPoolDTO> eventsByStartTimeAfter = events.stream()
                                .filter(event -> event.getStartTime().isAfter(dateTime))
                                .map(EventPoolDTO::new)
                                .toList();

                return eventsByStartTimeAfter;
        }

        /**
         * Retrieves all events that end before a specified date and time.
         * Useful for finding events that have concluded by a certain point.
         *
         * @param dateTime the LocalDateTime before which events should end
         * @return List containing events ending before the specified time
         */
        public List<EventPoolDTO> getEventsByEndTimeBefore(LocalDateTime dateTime) {
                List<Event> events = eventRepository.findByEndTimeBefore(dateTime);

                List<EventPoolDTO> eventsByEndTimeBefore = events.stream()
                                .filter(event -> event.getEndTime().isBefore(dateTime))
                                .map(EventPoolDTO::new)
                                .toList();

                return eventsByEndTimeBefore;
        }

        /**
         * Retrieves all events that have at least the specified number of free spots
         * available.
         * Calculates free slots by subtracting occupied count from total capacity.
         *
         * @param freeSlots the minimum number of free spots required
         * @return List containing events with sufficient available capacity
         */
        public List<EventPoolDTO> getEventsByFreeSlots(Integer freeSlots) {
                List<Event> events = eventRepository.findAll();

                List<EventPoolDTO> eventsByFreeSlots = events.stream()
                                .filter(event -> event.getCapacity() - event.getOccupied() >= freeSlots)
                                .map(EventPoolDTO::new)
                                .toList();

                return eventsByFreeSlots;
        }

        /**
         * Retrieves events filtered by multiple criteria including sport, skill level,
         * timing, and capacity.
         * All filter parameters are optional - null or empty values are ignored in
         * filtering.
         * Calculates free slots dynamically by comparing capacity with occupied count.
         *
         * @param sports         list of sport IDs to filter by (optional)
         * @param skillLevels    list of skill levels to filter by (optional)
         * @param startTimeAfter minimum start time for events (optional)
         * @param endTimeBefore  maximum end time for events (optional)
         * @param freeSlots      minimum number of free spots required (optional)
         * @return List containing events matching all specified criteria
         */
        public List<EventPoolDTO> getFilteredEvents(
                        List<Integer> sports,
                        List<Integer> skillLevels,
                        LocalDateTime startTimeAfter,
                        LocalDateTime endTimeBefore,
                        Integer freeSlots) {
                List<Event> events = eventRepository.findAll();

                List<EventPoolDTO> filteredEvents = events.stream()
                                .filter(event -> sports == null || sports.isEmpty()
                                                || sports.contains(event.getSport()))
                                .filter(event -> skillLevels == null || skillLevels.isEmpty()
                                                || skillLevels.contains(event.getSkillLevel()))
                                .filter(event -> startTimeAfter == null || event.getStartTime().isAfter(startTimeAfter))
                                .filter(event -> endTimeBefore == null || event.getEndTime().isBefore(endTimeBefore))
                                .filter(event -> event.getCapacity() - event.getOccupied() >= freeSlots)
                                .map(EventPoolDTO::new)
                                .toList();

                return filteredEvents;
        }

        public List<EventPoolDTO> getMyAttendedPastEvents(String username) {
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                List<EventPoolDTO> pastAttendedEvents = eventParticipantRepository
                                .findByUserIdAndRoleOfParticipant(user.getId(), 1)
                                .stream()
                                .filter(ep -> LocalDateTime.now().isAfter(ep.getEvent().getEndTime()))
                                .map(ep -> new EventPoolDTO(ep.getEvent()))
                                .toList();

                return pastAttendedEvents;
        }

        public List<EventPoolDTO> getMyAttendedUpcomingEvents(String username) {
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                List<EventPoolDTO> upcomingAttendedEvents = eventParticipantRepository
                                .findByUserIdAndRoleOfParticipant(user.getId(), 1)
                                .stream()
                                .filter(ep -> LocalDateTime.now().isBefore(ep.getEvent().getStartTime()))
                                .map(ep -> new EventPoolDTO(ep.getEvent()))
                                .toList();

                return upcomingAttendedEvents;
        }

        public boolean cancelEvent(UUID eventId, String username) {
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

                if (event.getOrganizer().getId().equals(user.getId())) {
                        event.setStatusOfEvent(3);
                        eventRepository.save(event);
                        notificationService.notifyEventCancelled(event);
                        return true;
                } else {
                        throw new IllegalArgumentException("You are not the organizer of this event.");
                }
        }

        public boolean updateEvent(String username, UUID eventId, EventUpdateRequest updateRequest) {

                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new IllegalArgumentException("User not found"));

                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

                if (event.getOrganizer().getId().equals(user.getId())) {

                        event.setTitle(updateRequest.getTitle());
                        event.setStartTime(updateRequest.getStartTime());
                        event.setEndTime(updateRequest.getEndTime());

                        eventRepository.save(event);

                        notificationService.notifyEventUpdate(event);

                        return true;
                } else {
                        return false;
                }
        }

        public EventDetailsDTO getEventDetails(UUID eventId){

                Event event = eventRepository.findById(eventId)
                                .orElseThrow(() -> new IllegalArgumentException("Event not found"));
        
                return mapToEventDetailsDTO(event);
        }

        private EventDetailsDTO mapToEventDetailsDTO(Event event){
                EventDetailsDTO dto = new EventDetailsDTO();
                dto.setTitle(event.getTitle());
                dto.setSport(event.getSport());
                dto.setAddress(event.getAddress());
                dto.setCapacity(event.getCapacity());
                dto.setOccupied(event.getOccupied());
                dto.setStartTime(event.getStartTime());
                dto.setEndTime(event.getEndTime());
                dto.setSkillLevel(event.getSkillLevel());
                dto.setLatitude(event.getLatitude());
                dto.setLongitude(event.getLongitude());
                dto.setParticipants(event.getParticipants()
                                .stream()
                                .map(ep -> {
                                        EventParticipantDTO participantDTO = new EventParticipantDTO();
                                        participantDTO.setUsername(ep.getUser().getUsername());
                                        return participantDTO;
                                })
                                .collect(Collectors.toSet()));
                return dto;
        }
}
