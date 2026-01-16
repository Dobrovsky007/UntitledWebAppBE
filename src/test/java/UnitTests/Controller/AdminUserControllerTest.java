package UnitTests.Controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.Eventified.controller.admin.AdminUserController;
import com.webapp.Eventified.dto.admin.UserInfoAdmin;
import com.webapp.Eventified.service.UserService;

class AdminUserControllerTest {

	private UserService userService;
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		userService = mock(UserService.class);
		AdminUserController controller = new AdminUserController(userService);

		ObjectMapper objectMapper = new ObjectMapper();
		mockMvc = MockMvcBuilders
				.standaloneSetup(controller)
			.setMessageConverters(
				new StringHttpMessageConverter(),
				new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter(objectMapper))
				.build();
	}

	@Test
	@DisplayName("GET /admin/users: empty list -> 500")
	void getAllUsers_empty() throws Exception {
		// Arrange
		when(userService.getAllUserInfoAdmin()).thenReturn(List.of());

		// Act + Assert
		mockMvc.perform(get("/admin/users"))
				.andExpect(status().isInternalServerError())
				.andExpect(content().string("No users found"));

		verify(userService).getAllUserInfoAdmin();
	}

	@Test
	@DisplayName("GET /admin/users: non-empty list -> 200 + JSON array")
	void getAllUsers_ok() throws Exception {
		// Arrange
		UserInfoAdmin dto = new UserInfoAdmin();
		dto.setId(UUID.fromString("33333333-3333-3333-3333-333333333333"));
		dto.setUsername("alice");
		dto.setEmail("a@example.com");
		dto.setIsVerified(true);
		dto.setIsAdmin(false);
		dto.setTrustScore(10.0f);

		when(userService.getAllUserInfoAdmin()).thenReturn(List.of(dto));

		// Act + Assert
		mockMvc.perform(get("/admin/users"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$[0].id").value("33333333-3333-3333-3333-333333333333"))
				.andExpect(jsonPath("$[0].username").value("alice"))
			.andExpect(jsonPath("$[0].email").value("a@example.com"))
			.andExpect(jsonPath("$[0].isVerified").value(true))
			.andExpect(jsonPath("$[0].isAdmin").value(false));

		// Controller calls the service twice: once for isEmpty(), once for the body.
		verify(userService, times(2)).getAllUserInfoAdmin();
	}
}
