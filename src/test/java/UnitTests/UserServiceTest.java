package UnitTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.webapp.Eventified.domain.User;
import com.webapp.Eventified.domain.SportUser;
import com.webapp.Eventified.domain.EventParticipant;
import com.webapp.Eventified.dto.UserProfileDTO;
import com.webapp.Eventified.repository.UserRepository;
import com.webapp.Eventified.repository.EventParticipantRepository;
import com.webapp.Eventified.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.UUID;
import java.util.Optional;

class UserServiceTest {

    private UserRepository userRepository;
    private EventParticipantRepository eventParticipantRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        eventParticipantRepository = mock(EventParticipantRepository.class);
        userService = new UserService(userRepository, eventParticipantRepository);
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
}