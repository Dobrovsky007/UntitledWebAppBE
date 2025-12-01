package com.webapp.Eventified;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the Eventified backend application.
 * Enables Spring Boot auto-configuration and scheduling support.
 *
 * @author Eventified Team
 * @version 1.0
 */
@SpringBootApplication
@EnableScheduling
public class EventifiedApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventifiedApplication.class, args);
	}
}
