package UnitTests.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.webapp.Eventified.dto.user.EventDetailsDTO;
import com.webapp.Eventified.dto.user.EventPoolDTO;
import com.webapp.Eventified.dto.user.EventUpdateRequest;
import com.webapp.Eventified.model.Event;
import com.webapp.Eventified.model.EventParticipant;
import com.webapp.Eventified.model.User;
import com.webapp.Eventified.repository.EventParticipantRepository;
import com.webapp.Eventified.repository.EventRepository;
import com.webapp.Eventified.repository.UserRepository;
import com.webapp.Eventified.service.EventService;
import com.webapp.Eventified.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

class EventServiceTest {

    private EventRepository eventRepository;
    private UserRepository userRepository;
    private EventParticipantRepository eventParticipantRepository;
    private NotificationService notificationService;
    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        userRepository = mock(UserRepository.class);
        eventParticipantRepository = mock(EventParticipantRepository.class);
        notificationService = mock(NotificationService.class);
        eventService = new EventService(eventRepository, userRepository, eventParticipantRepository, notificationService);
    }

    @Test
    @DisplayName("createEvent: success")
    void createEvent_success() {
        // Arrange
        String username = "testuser";
        String title = "Soccer Match";
        Integer sport = 1;
        String address = "123 Main St";
        Integer skillLevel = 2;
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        LocalDateTime endTime = startTime.plusHours(2);
        Integer capacity = 10;
        BigDecimal latitude = BigDecimal.valueOf(40.7128);
        BigDecimal longitude = BigDecimal.valueOf(-74.0060);

        User organizer = new User();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(organizer));
        when(eventRepository.findByTitleAndOrganizer(title, organizer)).thenReturn(Optional.empty());
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Event event = eventService.createEvent(username, title, sport, address, skillLevel, startTime, endTime, capacity, latitude, longitude);

        // Assert
        assertAll(
            () -> assertEquals(title, event.getTitle()),
            () -> assertEquals(organizer, event.getOrganizer()),
            () -> assertEquals(sport, event.getSport()),
            () -> assertEquals(address, event.getAddress()),
            () -> assertEquals(skillLevel, event.getSkillLevel()),
            () -> assertEquals(startTime, event.getStartTime()),
            () -> assertEquals(endTime, event.getEndTime()),
            () -> assertEquals(capacity, event.getCapacity()),
            () -> assertEquals(latitude, event.getLatitude()),
            () -> assertEquals(longitude, event.getLongitude())
        );

        // Verify
        verify(userRepository).findByUsername(username);
        verify(eventRepository).findByTitleAndOrganizer(title, organizer);
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    @DisplayName("createEvent: user not found throws")
    void createEvent_userNotFound_throwsException() {
        // Arrange
        String username = "unknown";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            eventService.createEvent(username, "title", 1, "address", 1, LocalDateTime.now(), LocalDateTime.now().plusHours(1), 5, BigDecimal.ONE, BigDecimal.ONE)
        );

        // Assert
        assertEquals("User not found", exception.getMessage());

        // Verify
        verify(userRepository).findByUsername(username);
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("createEvent: duplicate title throws")
    void createEvent_duplicateTitle_throwsException() {
        // Arrange
        String username = "testuser";
        String title = "Soccer Match";
        User organizer = new User();
        Event existingEvent = new Event();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(organizer));
        when(eventRepository.findByTitleAndOrganizer(title, organizer)).thenReturn(Optional.of(existingEvent));

        // Act
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            eventService.createEvent(username, title, 1, "address", 1, LocalDateTime.now(), LocalDateTime.now().plusHours(1), 5, BigDecimal.ONE, BigDecimal.ONE)
        );

        // Assert
        assertEquals("Event with the same title already exists for this user.", exception.getMessage());

        // Verify
        verify(userRepository).findByUsername(username);
        verify(eventRepository).findByTitleAndOrganizer(title, organizer);
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("getAllEvents: returns all events")
    void getAllEvents_returnsAllEvents() {
        // Arrange
        User organizer = new User();
        organizer.setId(UUID.randomUUID());

        Event event1 = new Event(organizer, "E1", 1, 1, "Addr1", BigDecimal.ONE, BigDecimal.ONE,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2), 10);
        Event event2 = new Event(organizer, "E2", 2, 2, "Addr2", BigDecimal.ONE, BigDecimal.ONE,
                LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2).plusHours(2), 12);

        when(eventRepository.findAll()).thenReturn(List.of(event1, event2));

        // Act
        List<EventPoolDTO> result = eventService.getAllEvents();

        // Assert
        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals("E1", result.get(0).getTitle()),
                () -> assertEquals("E2", result.get(1).getTitle())
        );

        // Verify
        verify(eventRepository).findAll();
    }

    @Test
    @DisplayName("getHostedEventsUpcoming: success")
    void getHostedEventsUpcoming_success() {
        // Arrange
        String username = "testuser";
        User organizer = new User();
        organizer.setId(UUID.randomUUID());

        Event upcoming = new Event(organizer, "Upcoming", 1, 1, "Addr", BigDecimal.ONE, BigDecimal.ONE,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2), 10);
        Event past = new Event(organizer, "Past", 1, 1, "Addr", BigDecimal.ONE, BigDecimal.ONE,
                LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(3).plusHours(2), 10);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(organizer));
        when(eventRepository.findByOrganizer(organizer)).thenReturn(List.of(upcoming, past));

        // Act
        List<EventPoolDTO> result = eventService.getHostedEventsUpcoming(username);

        // Assert
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals("Upcoming", result.get(0).getTitle())
        );

        // Verify
        verify(userRepository).findByUsername(username);
        verify(eventRepository).findByOrganizer(organizer);
    }

    @Test
    @DisplayName("getHostedEventsUpcoming: user not found throws")
    void getHostedEventsUpcoming_userNotFound_throwsException() {
        // Arrange
        String username = "unknown";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                eventService.getHostedEventsUpcoming(username)
        );

        // Assert
        assertEquals("User not found", exception.getMessage());

        // Verify
        verify(userRepository).findByUsername(username);
        verify(eventRepository, never()).findByOrganizer(any());
    }

    @Test
    @DisplayName("getHostedEventsPast: success")
    void getHostedEventsPast_success() {
        // Arrange
        String username = "testuser";
        User organizer = new User();
        organizer.setId(UUID.randomUUID());

        Event upcoming = new Event(organizer, "Upcoming", 1, 1, "Addr", BigDecimal.ONE, BigDecimal.ONE,
                LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2).plusHours(2), 10);
        Event past = new Event(organizer, "Past", 1, 1, "Addr", BigDecimal.ONE, BigDecimal.ONE,
                LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(2).plusHours(2), 10);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(organizer));
        when(eventRepository.findByOrganizer(organizer)).thenReturn(List.of(upcoming, past));

        // Act
        List<EventPoolDTO> result = eventService.getHostedEventsPast(username);

        // Assert
        assertAll(
                () -> assertEquals(1, result.size()),
                () -> assertEquals("Past", result.get(0).getTitle())
        );

        // Verify
        verify(userRepository).findByUsername(username);
        verify(eventRepository).findByOrganizer(organizer);
    }

    @Test
    @DisplayName("getHostedEventsPast: user not found throws")
    void getHostedEventsPast_userNotFound_throwsException() {
        // Arrange
        String username = "unknown";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                eventService.getHostedEventsPast(username)
        );

        // Assert
        assertEquals("User not found", exception.getMessage());

        // Verify
        verify(userRepository).findByUsername(username);
        verify(eventRepository, never()).findByOrganizer(any());
    }

    @Test
    @DisplayName("getEventsBySport: returns filtered events")
    void getEventsBySport_returnsFilteredEvents() {
        // Arrange
        User organizer = new User();
        organizer.setId(UUID.randomUUID());

        Integer sportId = 1;
        Event matching = new Event(organizer, "Sport1", sportId, 1, "Addr", BigDecimal.ONE, BigDecimal.ONE,
            LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2), 10);
        Event notMatching = new Event(organizer, "Sport2", 2, 1, "Addr", BigDecimal.ONE, BigDecimal.ONE,
            LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2), 10);

        when(eventRepository.findBySport(sportId)).thenReturn(List.of(matching, notMatching));

        // Act
        List<EventPoolDTO> result = eventService.getEventsBySport(sportId);

        // Assert
        assertAll(
            () -> assertEquals(1, result.size()),
            () -> assertEquals("Sport1", result.get(0).getTitle())
        );

        // Verify
        verify(eventRepository).findBySport(sportId);
        }

    @Test
    @DisplayName("getEventsBySkillLevel: returns filtered events")
    void getEventsBySkillLevel_returnsFilteredEvents() {
        // Arrange
        User organizer = new User();
        organizer.setId(UUID.randomUUID());

        Integer skillLevel = 2;
        Event matching = new Event(organizer, "S2", 1, skillLevel, "Addr", BigDecimal.ONE, BigDecimal.ONE,
            LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2), 10);
        Event notMatching = new Event(organizer, "S1", 1, 1, "Addr", BigDecimal.ONE, BigDecimal.ONE,
            LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2), 10);

        when(eventRepository.findBySkillLevel(skillLevel)).thenReturn(List.of(matching, notMatching));

        // Act
        List<EventPoolDTO> result = eventService.getEventsBySkillLevel(skillLevel);

        // Assert
        assertAll(
            () -> assertEquals(1, result.size()),
            () -> assertEquals("S2", result.get(0).getTitle())
        );

        // Verify
        verify(eventRepository).findBySkillLevel(skillLevel);
        }

    @Test
    @DisplayName("getEventsByStartTimeAfter: returns filtered events")
    void getEventsByStartTimeAfter_returnsFilteredEvents() {
        // Arrange
        User organizer = new User();
        organizer.setId(UUID.randomUUID());

        LocalDateTime after = LocalDateTime.now().plusHours(1);
        Event matching = new Event(organizer, "After", 1, 1, "Addr", BigDecimal.ONE, BigDecimal.ONE,
            after.plusHours(1), after.plusHours(3), 10);
        Event notMatching = new Event(organizer, "Before", 1, 1, "Addr", BigDecimal.ONE, BigDecimal.ONE,
            after.minusHours(2), after.minusHours(1), 10);

        when(eventRepository.findByStartTimeAfter(after)).thenReturn(List.of(matching, notMatching));

        // Act
        List<EventPoolDTO> result = eventService.getEventsByStartTimeAfter(after);

        // Assert
        assertAll(
            () -> assertEquals(1, result.size()),
            () -> assertEquals("After", result.get(0).getTitle())
        );

        // Verify
        verify(eventRepository).findByStartTimeAfter(after);
        }

    @Test
    @DisplayName("getEventsByEndTimeBefore: returns filtered events")
    void getEventsByEndTimeBefore_returnsFilteredEvents() {
        // Arrange
        User organizer = new User();
        organizer.setId(UUID.randomUUID());

        LocalDateTime before = LocalDateTime.now().plusHours(10);
        Event matching = new Event(organizer, "EndsBefore", 1, 1, "Addr", BigDecimal.ONE, BigDecimal.ONE,
            LocalDateTime.now().plusHours(1), before.minusHours(1), 10);
        Event notMatching = new Event(organizer, "EndsAfter", 1, 1, "Addr", BigDecimal.ONE, BigDecimal.ONE,
            LocalDateTime.now().plusHours(1), before.plusHours(1), 10);

        when(eventRepository.findByEndTimeBefore(before)).thenReturn(List.of(matching, notMatching));

        // Act
        List<EventPoolDTO> result = eventService.getEventsByEndTimeBefore(before);

        // Assert
        assertAll(
            () -> assertEquals(1, result.size()),
            () -> assertEquals("EndsBefore", result.get(0).getTitle())
        );

        // Verify
        verify(eventRepository).findByEndTimeBefore(before);
        }

    @Test
    @DisplayName("getEventsByFreeSlots: returns filtered events")
    void getEventsByFreeSlots_returnsFilteredEvents() {
        // Arrange
        User organizer = new User();
        organizer.setId(UUID.randomUUID());

        Event plenty = new Event(organizer, "Plenty", 1, 1, "Addr", BigDecimal.ONE, BigDecimal.ONE,
            LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2), 10);
        plenty.setOccupied(2);

        Event full = new Event(organizer, "Full", 1, 1, "Addr", BigDecimal.ONE, BigDecimal.ONE,
            LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2), 10);
        full.setOccupied(9);

        when(eventRepository.findAll()).thenReturn(List.of(plenty, full));

        // Act
        List<EventPoolDTO> result = eventService.getEventsByFreeSlots(5);

        // Assert
        assertAll(
            () -> assertEquals(1, result.size()),
            () -> assertEquals("Plenty", result.get(0).getTitle())
        );

        // Verify
        verify(eventRepository).findAll();
        }

    @Test
    @DisplayName("getFilteredEvents: all filters applied returns filtered")
    void getFilteredEvents_withAllFilters_returnsFilteredEvents() {
        // Arrange
        User organizer = new User();
        organizer.setId(UUID.randomUUID());

        Event match = new Event(organizer, "Match", 1, 2, "Addr", BigDecimal.ONE, BigDecimal.ONE,
            LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2).plusHours(2), 10);
        match.setOccupied(2);

        Event wrongSport = new Event(organizer, "WrongSport", 2, 2, "Addr", BigDecimal.ONE, BigDecimal.ONE,
            LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2).plusHours(2), 10);
        wrongSport.setOccupied(2);

        Event tooFull = new Event(organizer, "TooFull", 1, 2, "Addr", BigDecimal.ONE, BigDecimal.ONE,
            LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2).plusHours(2), 10);
        tooFull.setOccupied(9);

        when(eventRepository.findAll()).thenReturn(List.of(match, wrongSport, tooFull));

        // Act
        List<EventPoolDTO> result = eventService.getFilteredEvents(
            List.of(1),
            List.of(2),
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(3),
            5
        );

        // Assert
        assertAll(
            () -> assertEquals(1, result.size()),
            () -> assertEquals("Match", result.get(0).getTitle())
        );

        // Verify
        verify(eventRepository).findAll();
        }

    @Test
    @DisplayName("getFilteredEvents: no filters returns all")
    void getFilteredEvents_withNoFilters_returnsAllEvents() {
        // Arrange
        User organizer = new User();
        organizer.setId(UUID.randomUUID());

        Event e1 = new Event(organizer, "E1", 1, 1, "Addr", BigDecimal.ONE, BigDecimal.ONE,
            LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2), 10);
        Event e2 = new Event(organizer, "E2", 2, 2, "Addr", BigDecimal.ONE, BigDecimal.ONE,
            LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2).plusHours(2), 10);

        when(eventRepository.findAll()).thenReturn(List.of(e1, e2));

        // Act
        List<EventPoolDTO> result = eventService.getFilteredEvents(null, null, null, null, null);

        // Assert
        assertEquals(2, result.size());

        // Verify
        verify(eventRepository).findAll();
    }

    @Test
    @DisplayName("getMyAttendedPastEvents: success")
    void getMyAttendedPastEvents_success() {
        // Arrange
        String username = "testuser";
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        User organizer = new User();
        organizer.setId(UUID.randomUUID());
        Event past = new Event(organizer, "Past", 1, 1, "Addr", BigDecimal.ONE, BigDecimal.ONE,
            LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(3).plusHours(2), 10);

        EventParticipant ep = new EventParticipant(userId, UUID.randomUUID());
        ep.setEvent(past);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(eventParticipantRepository.findByUserIdAndRoleOfParticipant(userId, 1)).thenReturn(Optional.of(ep));

        // Act
        List<EventPoolDTO> result = eventService.getMyAttendedPastEvents(username);

        // Assert
        assertAll(
            () -> assertEquals(1, result.size()),
            () -> assertEquals("Past", result.get(0).getTitle())
        );

        // Verify
        verify(userRepository).findByUsername(username);
        verify(eventParticipantRepository).findByUserIdAndRoleOfParticipant(userId, 1);
        }

    @Test
    @DisplayName("getMyAttendedUpcomingEvents: success")
    void getMyAttendedUpcomingEvents_success() {
        // Arrange
        String username = "testuser";
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        User organizer = new User();
        organizer.setId(UUID.randomUUID());
        Event upcoming = new Event(organizer, "Upcoming", 1, 1, "Addr", BigDecimal.ONE, BigDecimal.ONE,
            LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2).plusHours(2), 10);

        EventParticipant ep = new EventParticipant(userId, UUID.randomUUID());
        ep.setEvent(upcoming);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(eventParticipantRepository.findByUserIdAndRoleOfParticipant(userId, 1)).thenReturn(Optional.of(ep));

        // Act
        List<EventPoolDTO> result = eventService.getMyAttendedUpcomingEvents(username);

        // Assert
        assertAll(
            () -> assertEquals(1, result.size()),
            () -> assertEquals("Upcoming", result.get(0).getTitle())
        );

        // Verify
        verify(userRepository).findByUsername(username);
        verify(eventParticipantRepository).findByUserIdAndRoleOfParticipant(userId, 1);
        }

    @Test
    @DisplayName("cancelEvent: success")
    void cancelEvent_success() {
        // Arrange
        String username = "organizer";
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();

        User organizer = new User();
        organizer.setId(userId);
        User authUser = new User();
        authUser.setId(userId);

        Event event = new Event(organizer, "Title", 1, 1, "Addr", BigDecimal.ONE, BigDecimal.ONE,
            LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2), 10);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(authUser));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        // Act
        boolean result = eventService.cancelEvent(eventId, username);

        // Assert
        assertAll(
            () -> assertTrue(result),
            () -> assertEquals(3, event.getStatusOfEvent())
        );

        // Verify
        verify(userRepository).findByUsername(username);
        verify(eventRepository).findById(eventId);
        verify(eventRepository).save(event);
        verify(notificationService).notifyEventCancelled(event);
        }

    @Test
    @DisplayName("cancelEvent: not organizer throws")
    void cancelEvent_notOrganizer_throwsException() {
        // Arrange
        String username = "notOrganizer";
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();

        User organizer = new User();
        organizer.setId(UUID.randomUUID());
        User authUser = new User();
        authUser.setId(userId);

        Event event = new Event(organizer, "Title", 1, 1, "Addr", BigDecimal.ONE, BigDecimal.ONE,
            LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2), 10);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(authUser));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        // Act
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                eventService.cancelEvent(eventId, username)
        );

        // Assert
        assertEquals("You are not the organizer of this event.", exception.getMessage());

        // Verify
        verify(userRepository).findByUsername(username);
        verify(eventRepository).findById(eventId);
        verify(eventRepository, never()).save(any(Event.class));
        verify(notificationService, never()).notifyEventCancelled(any(Event.class));
    }

    @Test
    @DisplayName("updateEvent: success")
    void updateEvent_success() {
        // Arrange
        String username = "organizer";
        UUID userId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();

        User organizer = new User();
        organizer.setId(userId);
        User authUser = new User();
        authUser.setId(userId);

        Event event = new Event(organizer, "Old", 1, 1, "Addr", BigDecimal.ONE, BigDecimal.ONE,
            LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2), 10);

        EventUpdateRequest update = new EventUpdateRequest();
        update.setTitle("NewTitle");
        update.setStartTime(LocalDateTime.now().plusDays(5));
        update.setEndTime(LocalDateTime.now().plusDays(5).plusHours(2));

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(authUser));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        // Act
        boolean result = eventService.updateEvent(username, eventId, update);

        // Assert
        assertAll(
            () -> assertTrue(result),
            () -> assertEquals("NewTitle", event.getTitle()),
            () -> assertEquals(update.getStartTime(), event.getStartTime()),
            () -> assertEquals(update.getEndTime(), event.getEndTime())
        );

        // Verify
        verify(userRepository).findByUsername(username);
        verify(eventRepository).findById(eventId);
        verify(eventRepository).save(event);
        verify(notificationService).notifyEventUpdate(event);
        }

    @Test
    @DisplayName("updateEvent: not organizer returns false")
    void updateEvent_notOrganizer_returnsFalse() {
        // Arrange
        String username = "notOrganizer";
        UUID eventId = UUID.randomUUID();

        User organizer = new User();
        organizer.setId(UUID.randomUUID());
        User authUser = new User();
        authUser.setId(UUID.randomUUID());

        Event event = new Event(organizer, "Old", 1, 1, "Addr", BigDecimal.ONE, BigDecimal.ONE,
            LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(2), 10);

        EventUpdateRequest update = new EventUpdateRequest();
        update.setTitle("NewTitle");
        update.setStartTime(LocalDateTime.now().plusDays(5));
        update.setEndTime(LocalDateTime.now().plusDays(5).plusHours(2));

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(authUser));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        // Act
        boolean result = eventService.updateEvent(username, eventId, update);

        // Assert
        assertFalse(result);

        // Verify
        verify(userRepository).findByUsername(username);
        verify(eventRepository).findById(eventId);
        verify(eventRepository, never()).save(any(Event.class));
        verify(notificationService, never()).notifyEventUpdate(any(Event.class));
    }

    @Test
    @DisplayName("getEventDetails: success")
    void getEventDetails_success() {
        // Arrange
        UUID eventId = UUID.randomUUID();

        User organizer = new User();
        organizer.setId(UUID.randomUUID());

        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.plusHours(2);

        Event event = new Event(organizer, "Title", 1, 2, "Addr", BigDecimal.ONE, BigDecimal.ONE, start, end, 10);
        event.setOccupied(3);

        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        // Act
        EventDetailsDTO dto = eventService.getEventDetails(eventId);

        // Assert
        assertAll(
            () -> assertEquals("Title", dto.getTitle()),
            () -> assertEquals(1, dto.getSport()),
            () -> assertEquals(2, dto.getSkillLevel()),
            () -> assertEquals("Addr", dto.getAddress()),
            () -> assertEquals(start, dto.getStartTime()),
            () -> assertEquals(end, dto.getEndTime()),
            () -> assertEquals(10, dto.getCapacity()),
            () -> assertEquals(3, dto.getOccupied()),
            () -> assertNotNull(dto.getParticipants())
        );

        // Verify
        verify(eventRepository).findById(eventId);
        }
}