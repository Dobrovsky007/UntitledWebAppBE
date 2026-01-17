package UnitTests.Controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.webapp.Eventified.controller.user.FriendshipController;
import com.webapp.Eventified.service.FriendshipService;

class FriendshipControllerTest {

	private FriendshipService friendshipService;
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		friendshipService = mock(FriendshipService.class);
		FriendshipController controller = new FriendshipController(friendshipService);
		mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
	}

	private static TestingAuthenticationToken auth(String username) {
		return new TestingAuthenticationToken(username, "pw", "ROLE_USER");
	}

	@Test
	@DisplayName("POST /friend-request/send/{addresseeId}: success -> 200")
	void sendFriendRequest_ok() throws Exception {
		// Arrange
		UUID addresseeId = UUID.randomUUID();
		when(friendshipService.sendFriendRequest("alice", addresseeId)).thenReturn(true);

		// Act + Assert
		mockMvc.perform(post("/friend-request/send/{addresseeId}", addresseeId)
						.principal(auth("alice")))
				.andExpect(status().isOk())
				.andExpect(content().string("Friend request sent"));

		verify(friendshipService).sendFriendRequest("alice", addresseeId);
	}

	@Test
	@DisplayName("POST /friend-request/send/{addresseeId}: failure -> 500")
	void sendFriendRequest_failure() throws Exception {
		// Arrange
		UUID addresseeId = UUID.randomUUID();
		when(friendshipService.sendFriendRequest("alice", addresseeId)).thenReturn(false);

		// Act + Assert
		mockMvc.perform(post("/friend-request/send/{addresseeId}", addresseeId)
						.principal(auth("alice")))
				.andExpect(status().isInternalServerError())
				.andExpect(content().string("Failed to send friend request"));

		verify(friendshipService).sendFriendRequest("alice", addresseeId);
	}

	@Test
	@DisplayName("POST /friend-request/accept/{requesterId}: success -> 200")
	void acceptFriendRequest_ok() throws Exception {
		// Arrange
		UUID requesterId = UUID.randomUUID();
		when(friendshipService.acceptFriendRequest(requesterId, "alice")).thenReturn(true);

		// Act + Assert
		mockMvc.perform(post("/friend-request/accept/{requesterId}", requesterId)
						.principal(auth("alice")))
				.andExpect(status().isOk())
				.andExpect(content().string("Friend request accepted"));

		verify(friendshipService).acceptFriendRequest(requesterId, "alice");
	}
}
