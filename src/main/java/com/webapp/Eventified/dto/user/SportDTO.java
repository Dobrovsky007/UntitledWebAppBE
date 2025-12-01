package com.webapp.Eventified.dto.user;

import lombok.Data;

/**
 * Data Transfer Object for sport-related information.
 * Represents a sport and the user's skill level in that sport.
 *
 * @author Eventified Team
 * @version 1.0
 */
@Data
public class SportDTO {
    private Integer sport;
    private Integer skillLevel;
}
