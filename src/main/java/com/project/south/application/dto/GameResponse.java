package com.project.south.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GameResponse {
    private String id;
    private String title;
    private String description;
    private int totalSlots;
    private int confirmedSlots;
    private int availableSlots;
    private boolean registrationOpen;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}