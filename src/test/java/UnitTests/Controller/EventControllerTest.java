package UnitTests.Controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.webapp.Eventified.controller.user.EventController;
import com.webapp.Eventified.dto.user.EventDetailsDTO;
import com.webapp.Eventified.dto.user.EventPoolDTO;
import com.webapp.Eventified.model.Event;
import com.webapp.Eventified.service.EventService;

class EventControllerTest {

    private EventService eventService;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
	eventService = mock(EventService.class);
	EventController controller = new EventController(eventService);

	objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
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

    private static EventPoolDTO dto(UUID id, String title) {
	Event e = new Event();
	e.setId(id);
	e.setTitle(title);
	e.setSport(1);
	e.setAddress("Addr");
	e.setStartTime(LocalDateTime.of(2026, 1, 1, 10, 0));
	e.setCapacity(10);
	e.setOccupied(2);
	e.setSkillLevel(3);
	return new EventPoolDTO(e);
    }

    @Test
    @DisplayName("POST /event/create: success -> 201")
    void createEvent_ok() throws Exception {
	String body = "{" +
		"\"title\":\"T\"," +
		"\"sport\":1," +
		"\"address\":\"Addr\"," +
		"\"skillLevel\":2," +
		"\"startTime\":\"2026-01-16T10:00:00\"," +
		"\"endTime\":\"2026-01-16T12:00:00\"," +
		"\"capacity\":10," +
		"\"latitude\":48.7," +
		"\"longitude\":21.2" +
		"}";

	// Act + Assert
	mockMvc.perform(post("/event/create")
			.principal(auth("alice"))
			.contentType(MediaType.APPLICATION_JSON)
			.content(body))
		.andExpect(status().isCreated())
		.andExpect(content().string("Event created successfully"));

	verify(eventService).createEvent(
		eq("alice"),
		eq("T"),
		eq(1),
		eq("Addr"),
		eq(2),
		any(LocalDateTime.class),
		any(LocalDateTime.class),
		eq(10),
		eq(new BigDecimal("48.7")),
		eq(new BigDecimal("21.2")));
    }

}
