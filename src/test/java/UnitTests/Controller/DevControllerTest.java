package UnitTests.Controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.Eventified.controller.user.DevController;

class DevControllerTest {

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		DevController controller = new DevController();
		ReflectionTestUtils.setField(controller, "activeProfile", "dev");

		ObjectMapper objectMapper = new ObjectMapper();
		mockMvc = MockMvcBuilders
				.standaloneSetup(controller)
				.setMessageConverters(new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter(objectMapper))
				.build();
	}

	@Test
	@DisplayName("GET /dev/status: returns status JSON")
	void status_ok() throws Exception {
		mockMvc.perform(get("/dev/status"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.status").value("OK"))
				.andExpect(jsonPath("$.message").value("Backend is running in development mode"))
				.andExpect(jsonPath("$.profile").value("dev"))
				.andExpect(jsonPath("$.security").value("DISABLED"))
				.andExpect(jsonPath("$.timestamp").isNumber());
	}

	@Test
	@DisplayName("GET /dev/endpoints: returns endpoint info JSON")
	void endpoints_ok() throws Exception {
		mockMvc.perform(get("/dev/endpoints"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.message").value("All endpoints are accessible in development mode"))
				.andExpect(jsonPath("$.swagger_ui").value("/swagger-ui.html"))
				.andExpect(jsonPath("$.api_docs").value("/v3/api-docs"))
				.andExpect(jsonPath("$.available_endpoints.Auth").value("/api/auth/*"))
				.andExpect(jsonPath("$.available_endpoints.Users").value("/api/user/*"))
				.andExpect(jsonPath("$.available_endpoints.Events").value("/api/events/*"))
				.andExpect(jsonPath("$.available_endpoints.Admin").value("/api/admin/*"))
				.andExpect(jsonPath("$.available_endpoints.Development").value("/api/dev/*"));
	}
}
