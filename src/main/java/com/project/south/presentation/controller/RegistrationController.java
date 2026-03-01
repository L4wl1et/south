package com.project.south.presentation.controller;

import com.project.south.application.dto.RegistrationRequest;
import com.project.south.application.dto.RegistrationResponse;
import com.project.south.application.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/{gameId}/registrations")
    public ResponseEntity<RegistrationResponse> register(@PathVariable String gameId,
                                                         @Valid @RequestBody RegistrationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registrationService.register(gameId, request));
    }

    @GetMapping("/{gameId}/registrations")
    public ResponseEntity<List<RegistrationResponse>> findByGameId(@PathVariable String gameId) {
        return ResponseEntity.ok(registrationService.findByGameId(gameId));
    }

    @GetMapping("/{gameId}/registrations/{id}")
    public ResponseEntity<RegistrationResponse> findById(@PathVariable String gameId,
                                                         @PathVariable String id) {
        return ResponseEntity.ok(registrationService.findById(id));
    }

    @PatchMapping("/{gameId}/registrations/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable String gameId,
                                       @PathVariable String id) {
        registrationService.cancel(id);
        return ResponseEntity.noContent().build();
    }
}