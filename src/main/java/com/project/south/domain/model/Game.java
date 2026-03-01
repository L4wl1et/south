package com.project.south.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "games")
public class Game {

    @Id
    private String id;

    private String title;
    private String description;
    private int totalSlots;
    private int confirmedSlots;
    private boolean registrationOpen;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    public int getAvailableSlots() {
        return totalSlots - confirmedSlots;
    }

    public boolean hasAvailableSlots() {
        return confirmedSlots < totalSlots;
    }
}