package com.project.south.application.dto;

import com.project.south.domain.model.RegistrationStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RegistrationResponse {
    private String id;
    private String gameId;
    private String playerName;
    private String email;
    private RegistrationStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
}