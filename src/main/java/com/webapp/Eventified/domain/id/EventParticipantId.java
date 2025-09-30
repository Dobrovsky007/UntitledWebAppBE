package com.webapp.Eventified.domain.id;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventParticipantId implements Serializable {
    private UUID userId;
    private UUID eventId;
}
