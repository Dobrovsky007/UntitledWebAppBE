package com.webapp.Eventified;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EventifiedApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventifiedApplication.class, args);
	}
}
