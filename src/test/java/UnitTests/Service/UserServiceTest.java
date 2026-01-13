package UnitTests.Service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.webapp.Eventified.dto.admin.UserInfoAdmin;
import com.webapp.Eventified.dto.user.SportDTO;
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
    @DisplayName("getOtherUserInfo: success")
    void getOtherUserInfo_success() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setUsername("testuser");
        user.setTrustScore(10);
        user.setNumberOfReviews(2);
        user.setVerified(true);
        user.setSports(new HashSet<>(Arrays.asList(new SportUser())));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        UserProfileDTO dto = userService.getOtherUserInfo(userId);

        // Assert
        assertAll(
                () -> assertEquals("testuser", dto.getUsername()),
                () -> assertEquals(5.0f, dto.getRating()),
                () -> assertTrue(dto.isVerified()),
                () -> assertNotNull(dto.getSports())
        );

        // Verify
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("getOtherUserInfo: user not found throws")
    void getOtherUserInfo_userNotFound_throwsException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(IllegalAccessError.class, () -> userService.getOtherUserInfo(userId));

        // Verify
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("getUserInfoByUsername: success")
    void getUserInfoByUsername_success() {
        // Arrange
        String username = "testuser";
        User user = new User();
        user.setUsername(username);
        user.setTrustScore(10);
        user.setNumberOfReviews(2);
        user.setVerified(false);
        user.setSports(new HashSet<>(Arrays.asList(new SportUser())));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        UserProfileDTO dto = userService.getUserInfoByUsername(username);

        // Assert
        assertAll(
                () -> assertEquals(username, dto.getUsername()),
                () -> assertEquals(5.0f, dto.getRating()),
                () -> assertFalse(dto.isVerified()),
                () -> assertNotNull(dto.getSports())
        );

        // Verify
        verify(userRepository).findByUsername(username);
    }

    @Test
    @DisplayName("getUserInfoByUsername: user not found throws")
    void getUserInfoByUsername_userNotFound_throwsException() {
        // Arrange
        String username = "unknown";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(IllegalAccessError.class, () -> userService.getUserInfoByUsername(username));

        // Verify
        verify(userRepository).findByUsername(username);
    }

    @Test
    @DisplayName("joinEvent: success")
    void joinEvent_success() {
        // Arrange
        String username = "testuser";
        UUID eventId = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(eventParticipantRepository.findByUserIdAndEventId(user.getId(), eventId)).thenReturn(Optional.empty());

        User organizer = new User();
        organizer.setId(UUID.randomUUID());
        com.webapp.Eventified.model.Event event = new com.webapp.Eventified.model.Event();
        event.setOrganizer(organizer);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        // Act
        boolean result = userService.joinEvent(username, eventId);

        // Assert
        assertTrue(result);

        // Verify
        verify(userRepository).findByUsername(username);
        verify(eventParticipantRepository).findByUserIdAndEventId(user.getId(), eventId);
        verify(eventParticipantRepository).save(any(EventParticipant.class));
        verify(eventRepository).findById(eventId);
    }

    @Test
    @DisplayName("joinEvent: user not found throws")
    void joinEvent_userNotFound_throwsException() {
        // Arrange
        String username = "unknown";
        UUID eventId = UUID.randomUUID();
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> userService.joinEvent(username, eventId));

        // Verify
        verify(userRepository).findByUsername(username);
    }

    @Test
    @DisplayName("joinEvent: already joined returns false")
    void joinEvent_alreadyJoined_returnsFalse() {
        // Arrange
        String username = "testuser";
        UUID eventId = UUID.randomUUID();
        User user = new User();
        user.setId(UUID.randomUUID());
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(eventParticipantRepository.findByUserIdAndEventId(user.getId(), eventId)).thenReturn(Optional.of(new EventParticipant()));

        // Act
        boolean result = userService.joinEvent(username, eventId);

        // Assert
        assertFalse(result);

        // Verify
        verify(userRepository).findByUsername(username);
        verify(eventParticipantRepository).findByUserIdAndEventId(user.getId(), eventId);
        verify(eventParticipantRepository, never()).save(any(EventParticipant.class));
    }

    @Test
    @DisplayName("deleteUser: success")
    void deleteUser_success() {
        // Arrange
        String username = "testuser";
        User user = new User();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // Act
        boolean result = userService.deleteUser(username);

        // Assert
        assertTrue(result);

        // Verify
        verify(userRepository).findByUsername(username);
        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("deleteUser: user not found throws")
    void deleteUser_userNotFound_throwsException() {
        // Arrange
        String username = "unknown";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> userService.deleteUser(username));

        // Verify
        verify(userRepository).findByUsername(username);
    }

    @Test
    @DisplayName("Should successfully delete user from event")
    void leaveEvent_success() {
        // Arrange
        String username = "testUser";
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        EventParticipant eventParticipant = new EventParticipant(userId, eventId);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(eventParticipantRepository.findByUserIdAndEventId(userId,eventId)).thenReturn(Optional.of(eventParticipant));

        com.webapp.Eventified.model.Event event = new com.webapp.Eventified.model.Event();
        User organizer = new User();
        organizer.setId(UUID.randomUUID());
        event.setOrganizer(organizer);
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(event));

        // Act
        boolean result = userService.leaveEvent(username, eventId);

        // Assert
        assertTrue(result);

        // Verify
        verify(userRepository).findByUsername(username);
        verify(eventParticipantRepository).findByUserIdAndEventId(userId, eventId);
        verify(eventParticipantRepository).delete(eventParticipant);
        verify(eventRepository, atLeastOnce()).findById(eventId);
    }

    @Test
    @DisplayName("Should return exception when user want to leave event but is not participant")
    void leaveEvent_userNotParticipant_throwsException() {
        // Arrange
        String username = "testUser";
        UUID eventId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(eventParticipantRepository.findByUserIdAndEventId(userId,eventId)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> userService.leaveEvent(username, eventId));

        // Verify
        verify(userRepository).findByUsername(username);
        verify(eventParticipantRepository).findByUserIdAndEventId(userId, eventId);
    }

    @Test
    @DisplayName("Should return exception when user want to leave event but user not found")
    void leaveEvent_userNotFound_throwsException(){
        // Arrange
        String username = "testUser";
        UUID eventId = UUID.randomUUID();

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> userService.leaveEvent(username, eventId));

        // Verify
        verify(userRepository).findByUsername(username);
        verify(eventParticipantRepository, never()).findByUserIdAndEventId(any(), any());
    }

    @Test
    @DisplayName("Should add preferred sport to user successfully")
    void addPreferredSport_success(){
        // Arrange
        String username = "testUser";
        Integer sportId = 1;
        User user = new User();
        user.setId(UUID.randomUUID());
        

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(sportUserRepository.save(any(SportUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        SportUser result = userService.addPreferredSport(username, sportId, 1);

        // Assert
        assertAll(
            () -> assertNotNull(result),
            () -> assertEquals(sportId, result.getSport()),
            () -> assertEquals(1, result.getSkillLevel())
        );

        // Verify
        verify(userRepository).findByUsername(username);
        verify(sportUserRepository).save(any(SportUser.class));
    }

    @Test
    @DisplayName("Should throw exception when adding preferred sport to user but user not found")
    void addPreferredSport_userNotFound_throwsException(){
        // Arrange
        String username = "testUser";
        Integer sportId = 1;

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act + Assert
        assertThrows(IllegalArgumentException.class, () -> userService.addPreferredSport(username, sportId, 1));

        verify(userRepository).findByUsername(username);
        verify(sportUserRepository, never()).findById(any());
    }

}