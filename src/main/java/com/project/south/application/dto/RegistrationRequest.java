package com.project.south.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegistrationRequest {

    @NotBlank(message = "Player name is required")
    private String playerName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
}