package UnitTests;

import org.springframework.boot.SpringBootConfiguration;

/**
 * Minimal Spring Boot configuration for slice tests.
 *
 * Intentionally does NOT enable component scanning, so @WebMvcTest stays isolated.
 */
@SpringBootConfiguration
public class TestApplication {
}
