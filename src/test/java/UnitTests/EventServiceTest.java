package UnitTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.webapp.Eventified.domain.Event;
import com.webapp.Eventified.domain.User;
import com.webapp.Eventified.repository.EventParticipantRepository;
import com.webapp.Eventified.repository.EventRepository;
import com.webapp.Eventified.repository.UserRepository;
import com.webapp.Eventified.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

class EventServiceTest {

    private EventRepository eventRepository;
    private UserRepository userRepository;
    private EventParticipantRepository eventParticipantRepository;
    private EventService eventService;

    @BeforeEach
    void setUp() {
        eventRepository = mock(EventRepository.class);
        userRepository = mock(UserRepository.class);
        eventParticipantRepository = mock(EventParticipantRepository.class);
        eventService = new EventService(eventRepository, userRepository, eventParticipantRepository);
    }

    @Test
    void createEvent_success() {
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

        Event event = eventService.createEvent(username, title, sport, address, skillLevel, startTime, endTime, capacity, latitude, longitude);

        assertEquals(title, event.getTitle());
        assertEquals(organizer, event.getOrganizer());
        assertEquals(sport, event.getSport());
        assertEquals(address, event.getAddress());
        assertEquals(skillLevel, event.getSkillLevel());
        assertEquals(startTime, event.getStartTime());
        assertEquals(endTime, event.getEndTime());
        assertEquals(capacity, event.getCapacity());
        assertEquals(latitude, event.getLatitude());
        assertEquals(longitude, event.getLongitude());
    }

    @Test
    void createEvent_userNotFound_throwsException() {
        String username = "unknown";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            eventService.createEvent(username, "title", 1, "address", 1, LocalDateTime.now(), LocalDateTime.now().plusHours(1), 5, BigDecimal.ONE, BigDecimal.ONE)
        );
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void createEvent_duplicateTitle_throwsException() {
        String username = "testuser";
        String title = "Soccer Match";
        User organizer = new User();
        Event existingEvent = new Event();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(organizer));
        when(eventRepository.findByTitleAndOrganizer(title, organizer)).thenReturn(Optional.of(existingEvent));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            eventService.createEvent(username, title, 1, "address", 1, LocalDateTime.now(), LocalDateTime.now().plusHours(1), 5, BigDecimal.ONE, BigDecimal.ONE)
        );
        assertEquals("Event with the same title already exists for this user.", exception.getMessage());
    }
}