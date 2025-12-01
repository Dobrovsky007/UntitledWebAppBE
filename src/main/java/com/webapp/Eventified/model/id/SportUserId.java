package com.webapp.Eventified.model.id;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SportUserId implements Serializable {
    private UUID userId;
    private Integer sport;
}
