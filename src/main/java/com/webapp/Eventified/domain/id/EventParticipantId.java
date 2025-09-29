package com.webapp.Eventified.domain.id;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventParticipantId implements Serializable {
    private String userId;
    private String eventId;
}
