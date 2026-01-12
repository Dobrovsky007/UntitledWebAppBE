package UnitTests.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.webapp.Eventified.dto.user.UserProfileDTO;
import com.webapp.Eventified.model.EventParticipant;
import com.webapp.Eventified.model.SportUser;
import com.webapp.Eventified.model.User;
import com.webapp.Eventified.repository.EventRepository;
import com.webapp.Eventified.repository.SportUserRepository;
import com.webapp.Eventified.repository.UserRepository;
import com.webapp.Eventified.repository.EventParticipantRepository;
import com.webapp.Eventified.service.NotificationService;
import com.webapp.Eventified.service.UserService;
import org.junit.jupiter.api.*;


import java.util.*;


class UserServiceTest {

    private UserRepository userRepository;
    private EventParticipantRepository eventParticipantRepository;
    private UserService userService;
    private SportUserRepository sportUserRepository;
    private EventRepository eventRepository;
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        eventParticipantRepository = mock(EventParticipantRepository.class);
        sportUserRepository = mock(SportUserRepository.class);
        eventRepository = mock(EventRepository.class);
        notificationService = mock(NotificationService.class);
        userService = new UserService(userRepository, eventParticipantRepository, sportUserRepository, eventRepository, notificationService);
    }

    @Test
    void getOtherUserInfo_success() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setUsername("testuser");
        user.setTrustScore(10);
        user.setNumberOfReviews(2);
        user.setVerified(true);
        user.setSports(new HashSet<>(Arrays.asList(new SportUser())));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserProfileDTO dto = userService.getOtherUserInfo(userId);

        assertEquals("testuser", dto.getUsername());
        assertEquals(5.0f, dto.getRating());
        assertTrue(dto.isVerified());
        assertNotNull(dto.getSports());
    }

    @Test
    void getOtherUserInfo_userNotFound_throwsException() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(IllegalAccessError.class, () -> userService.getOtherUserInfo(userId));
    }

    @Test
    void getUserInfoByUsername_success() {
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        user.setTrustScore(10);
        user.setNumberOfReviews(2);
        user.setVerified(false);
        user.setSports(new HashSet<>(Arrays.asList(new SportUser())));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserProfileDTO dto = userService.getUserInfoByUsername(username);

        assertEquals(username, dto.getUsername());
        assertEquals(5.0f, dto.getRating());
        assertFalse(dto.isVerified());
        assertNotNull(dto.getSports());
    }

    @Test
    void getUserInfoByUsername_userNotFound_throwsException() {
        String username = "unknown";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(IllegalAccessError.class, () -> userService.getUserInfoByUsername(username));
    }

    @Test
    void joinEvent_success() {
        String username = "testuser";
        UUID eventId = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(eventParticipantRepository.findByUserIdAndEventId(user.getId(), eventId)).thenReturn(Optional.empty());

        boolean result = userService.joinEvent(username, eventId);

        assertTrue(result);
        verify(eventParticipantRepository).save(any(EventParticipant.class));
    }

    @Test
    void joinEvent_userNotFound_throwsException() {
        String username = "unknown";
        UUID eventId = UUID.randomUUID();
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.joinEvent(username, eventId));
    }

    @Test
    void joinEvent_alreadyJoined_returnsFalse() {
        String username = "testuser";
        UUID eventId = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(eventParticipantRepository.findByUserIdAndEventId(user.getId(), eventId)).thenReturn(Optional.of(new EventParticipant()));

        boolean result = userService.joinEvent(username, eventId);

        assertFalse(result);
        verify(eventParticipantRepository, never()).save(any(EventParticipant.class));
    }

    @Test
    void deleteUser_success() {
        String username = "testuser";
        User user = new User();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        boolean result = userService.deleteUser(username);

        assertTrue(result);
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_userNotFound_throwsException() {
        String username = "unknown";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(username));
    }

    @Test
    @DisplayName("Should successfully delete user from event")
    void leaveEvent_success() {

        String username = "testUser";
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        EventParticipant eventParticipant = new EventParticipant(userId, eventId);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(eventParticipantRepository.findByUserIdAndEventId(userId,eventId)).thenReturn(Optional.of(eventParticipant));

        boolean result = userService.leaveEvent(username, eventId);

        assertTrue(result);

        verify(userRepository).findByUsername(username);
        verify(eventParticipantRepository).findByUserIdAndEventId(userId, eventId);
        verify(eventParticipantRepository).delete(eventParticipant);
    }

    @Test
    @DisplayName("Should return exception when user want to leave event but is not participant")
    void leaveEvent_userNotParticipant_throwsException() {
        String username = "testUser";
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(eventParticipantRepository.findByUserIdAndEventId(userId,eventId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.leaveEvent(username, eventId));

        verify(userRepository).findByUsername(username);
        verify(eventParticipantRepository).findByUserIdAndEventId(userId, eventId);
    }

    @Test
    @DisplayName("Should return exception when user want to leave event but user not found")
    void leaveEvent_userNotFound_throwsException(){
        String username = "testUser";
        UUID eventId = UUID.randomUUID();

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.leaveEvent(username, eventId));

        verify(userRepository).findByUsername(username);
        verify(eventParticipantRepository, never()).findByUserIdAndEventId(any(), any());
    }

    @Test
    @DisplayName("Should add preferred sport to user successfully")
    void addPreferredSport_success(){
        String username = "testUser";
        Integer sportId = 1;
        User user = new User();
        user.setSports(new HashSet<>());
        

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(sportUserRepository.findById(any())).thenReturn(Optional.of(new SportUser()));

        SportUser result = userService.addPreferredSport(username, sportId, 1);

        assertNotNull(result);
        assertEquals(1, user.getSports().size());
        verify(userRepository).save(user);
    }

}