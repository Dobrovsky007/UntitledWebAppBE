package UnitTests.Controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webapp.Eventified.controller.user.AuthController;
import com.webapp.Eventified.dto.user.LoginResponse;
import com.webapp.Eventified.service.AuthService;

class AuthControllerTest {

	private AuthService authService;
	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		authService = mock(AuthService.class);
		AuthController controller = new AuthController(authService);

		ObjectMapper objectMapper = new ObjectMapper();
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/templates/");
		viewResolver.setSuffix(".html");

		mockMvc = MockMvcBuilders
				.standaloneSetup(controller)
			.setMessageConverters(
				new StringHttpMessageConverter(),
				new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter(objectMapper))
				.setViewResolvers(viewResolver)
				.build();
	}

	@Test
	@DisplayName("POST /auth/register: success -> 201 + message")
	void register_ok() throws Exception {
		// Act + Assert
		mockMvc.perform(post("/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"alice\",\"email\":\"a@example.com\",\"password\":\"pw\"}"))
				.andExpect(status().isCreated())
				.andExpect(content().string("User registered successfully"));

		verify(authService).registerUser("alice", "a@example.com", "pw");
	}

	@Test
	@DisplayName("POST /auth/register: service throws -> 400 + message")
	void register_badRequest() throws Exception {
		// Arrange
		doThrow(new IllegalArgumentException("Username taken"))
				.when(authService)
				.registerUser(anyString(), anyString(), anyString());

		// Act + Assert
		mockMvc.perform(post("/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"alice\",\"email\":\"a@example.com\",\"password\":\"pw\"}"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Username taken"));
	}

	@Test
	@DisplayName("POST /auth/login: returns 200 + token JSON")
	void login_ok() throws Exception {
		// Arrange
		when(authService.login(any())).thenReturn(new LoginResponse("jwt-token"));

		// Act + Assert
		mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"username\":\"alice\",\"password\":\"pw\"}"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.token").value("jwt-token"));
	}

	@Test
	@DisplayName("GET /auth/verify: verified -> returns success view")
	void verify_ok_returnsSuccessView() throws Exception {
		// Arrange
		when(authService.verifyUser("t")).thenReturn(true);

		// Act + Assert
		mockMvc.perform(get("/auth/verify").param("token", "t"))
				.andExpect(status().isOk())
				.andExpect(view().name("mail/registration-successful"))
				.andExpect(model().attributeExists("loginUrl"));
	}

	@Test
	@DisplayName("GET /auth/verify: exception -> returns error view")
	void verify_exception_returnsErrorView() throws Exception {
		// Arrange
		when(authService.verifyUser("t")).thenThrow(new RuntimeException("boom"));

		// Act + Assert
		mockMvc.perform(get("/auth/verify").param("token", "t"))
				.andExpect(status().isOk())
				.andExpect(view().name("error"));
	}
}
