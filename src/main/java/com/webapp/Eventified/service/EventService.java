package com.webapp.Eventified.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.webapp.Eventified.domain.Event;
import com.webapp.Eventified.domain.EventParticipant;
import com.webapp.Eventified.domain.User;
import com.webapp.Eventified.dto.EventPoolDTO;
import com.webapp.Eventified.repository.EventParticipantRepository;
import com.webapp.Eventified.repository.EventRepository;
import com.webapp.Eventified.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventParticipantRepository eventParticipantRepository;

        public Event createEvent(String username, String title, Integer sport, String address, Integer skillLevel, LocalDateTime startTime, LocalDateTime endTime, Integer capacity, BigDecimal latitude, BigDecimal longitude) {
    
            User organizer = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            if (eventRepository.findByTitleAndOrganizer(title, organizer).isPresent()){ 
                throw new IllegalArgumentException("Event with the same title already exists for this user.");
            }
    
            Event event = new Event(organizer, title, sport, skillLevel, address, latitude, longitude, startTime, endTime, capacity);
    
            return eventRepository.save(event);
        }

        public List <EventPoolDTO> getAllEvents() {
            List<Event> events = eventRepository.findAll();
            List<EventPoolDTO> eventsDTO = events.stream().map(EventPoolDTO::new).toList();
            return eventsDTO;
        }
    
        public List<EventPoolDTO> getMyEventsUpcoming(String username){
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            
            List <Event> events = eventRepository.findByOrganizer(user);
            List<EventPoolDTO> upcomingEventsDTO = events.stream()
                .filter(event -> LocalDateTime.now().isBefore(event.getStartTime()))
                .map(EventPoolDTO::new)
                .toList();
            return upcomingEventsDTO;
        }
    
        public List<EventPoolDTO> getMyEventsPast(String username){
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            List<Event> events = eventRepository.findByOrganizer(user);

            List<EventPoolDTO> pastEventsDTO = events.stream()
            .filter(event -> LocalDateTime.now().isAfter(event.getEndTime()))
            .map(EventPoolDTO:: new)
            .toList();
            
            return pastEventsDTO;
        }
    
        public List<EventPoolDTO> getEventsBySport(Integer sport){
            List<Event> events = eventRepository.findBySport(sport);
            
            List<EventPoolDTO> eventsBySport = events.stream()
            .filter(event -> event.getSport() == sport)
            .map(EventPoolDTO::new)
            .toList();

            return eventsBySport;
        }
    
        public List<EventPoolDTO> getEventsBySkillLevel(Integer skillLevel){
            List<Event> events = eventRepository.findBySkillLevel(skillLevel);

            List<EventPoolDTO> eventsBySkillLevel = events.stream()
            .filter(event -> event.getSkillLevel() == skillLevel)
            .map(EventPoolDTO:: new)
            .toList();

            return eventsBySkillLevel;
        }
        
        public List<EventPoolDTO> getEventsByStartTimeAfter(LocalDateTime dateTime){
            List<Event> events = eventRepository.findByStartTimeAfter(dateTime);

            List<EventPoolDTO> eventsByStartTimeAfter = events.stream()
            .filter(event -> event.getStartTime().isAfter(dateTime))
            .map(EventPoolDTO::new)
            .toList();

            return eventsByStartTimeAfter;
        }
    
        public List<EventPoolDTO> getEventsByEndTimeBefore(LocalDateTime dateTime){
            List<Event> events = eventRepository.findByEndTimeBefore(dateTime);

            List<EventPoolDTO> eventsByEndTimeBefore = events.stream()
            .filter(event -> event.getEndTime().isBefore(dateTime))
            .map(EventPoolDTO::new)
            .toList();

            return eventsByEndTimeBefore;
        }
        
        public List<EventPoolDTO> getEventsByFreeSlots(Integer freeSlots){
            List<Event> events = eventRepository.findAll();

            List<EventPoolDTO> eventsByFreeSlots = events.stream()
            .filter(event -> event.getCapacity() - event.getOccupied() >= freeSlots)
            .map(EventPoolDTO::new)
            .toList();

            return eventsByFreeSlots;
        }
    }
