package UnitTests.Controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.Eventified.controller.user.RatingController;
import com.webapp.Eventified.service.RatingService;

class RatingControllerTest {

	private RatingService ratingService;
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		ratingService = mock(RatingService.class);
		RatingController controller = new RatingController(ratingService);

		ObjectMapper objectMapper = new ObjectMapper();
		mockMvc = MockMvcBuilders
				.standaloneSetup(controller)
			.setMessageConverters(
				new StringHttpMessageConverter(),
				new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter(objectMapper))
				.build();
	}

	private static TestingAuthenticationToken auth(String username) {
		return new TestingAuthenticationToken(username, "pw", "ROLE_USER");
	}

	@Test
	@DisplayName("POST /ratings/event/{eventId}: success -> 200")
	void submitEventRatings_ok() throws Exception {
		// Arrange
		UUID eventId = UUID.randomUUID();

		// Act + Assert
		mockMvc.perform(post("/ratings/event/{eventId}", eventId)
						.principal(auth("alice"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"bob\":5}"))
				.andExpect(status().isOk())
				.andExpect(content().string("Ratings submitted successfully"));

		verify(ratingService).submitEventParticipantRatings(eq("alice"), eq(eventId), eq(Map.of("bob", 5)));
	}

	@Test
	@DisplayName("POST /ratings/event/{eventId}: service throws -> 400")
	void submitEventRatings_serviceThrows_badRequest() throws Exception {
		// Arrange
		UUID eventId = UUID.randomUUID();
		doThrow(new IllegalArgumentException("Event not ended"))
				.when(ratingService)
				.submitEventParticipantRatings(eq("alice"), eq(eventId), anyMap());

		// Act + Assert
		mockMvc.perform(post("/ratings/event/{eventId}", eventId)
						.principal(auth("alice"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"bob\":5}"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Event not ended"));
	}

	@Test
	@DisplayName("POST /ratings/event/{eventId}: invalid JSON -> 400")
	void submitEventRatings_invalidJson_badRequest() throws Exception {
		// Arrange
		UUID eventId = UUID.randomUUID();

		// Act + Assert
		mockMvc.perform(post("/ratings/event/{eventId}", eventId)
						.principal(auth("alice"))
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"bob\":\"five\"}"))
				.andExpect(status().isBadRequest());

		verifyNoInteractions(ratingService);
	}
}
