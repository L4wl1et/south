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
@Document(collection = "registrations")
public class Registration {

    @Id
    private String id;

    private String gameId;
    private String playerName;
    private String email;
    private RegistrationStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;

    @Version
    private Long version;
}