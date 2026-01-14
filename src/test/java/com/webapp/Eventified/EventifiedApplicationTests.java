package com.webapp.Eventified;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Disabled: integration-style Spring context test requires DB/Flyway; unit test suite must not open real connections")
class EventifiedApplicationTests {

	@Test
	void contextLoads() {
	}

}
