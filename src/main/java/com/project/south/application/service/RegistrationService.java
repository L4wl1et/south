package com.project.south.application.service;

import com.project.south.application.dto.RegistrationRequest;
import com.project.south.application.dto.RegistrationResponse;
import com.project.south.domain.exception.*;
import com.project.south.domain.model.Registration;
import com.project.south.domain.model.RegistrationStatus;
import com.project.south.domain.repository.GameRepository;
import com.project.south.domain.repository.RegistrationRepository;
import com.project.south.infrastructure.messaging.RegistrationEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final GameRepository gameRepository;
    private final RegistrationEventPublisher eventPublisher;

    public RegistrationResponse register(String gameId, RegistrationRequest request) {
        var game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId));

        if (!game.isRegistrationOpen()) {
            throw new RegistrationClosedException(gameId);
        }

        if (registrationRepository.existsByEmailAndGameId(request.getEmail(), gameId)) {
            throw new DuplicateRegistrationException(request.getEmail(), gameId);
        }

        Registration registration = Registration.builder()
                .gameId(gameId)
                .playerName(request.getPlayerName())
                .email(request.getEmail())
                .status(RegistrationStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();

        Registration saved = registrationRepository.save(registration);
        eventPublisher.publishRegistrationRequested(saved);

        return toResponse(saved);
    }

    public RegistrationResponse findById(String id) {
        return registrationRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new RegistrationNotFoundException(id));
    }

    public List<RegistrationResponse> findByGameId(String gameId) {
        if (!gameRepository.existsById(gameId)) {
            throw new GameNotFoundException(gameId);
        }
        return registrationRepository.findByGameId(gameId).stream()
                .map(this::toResponse)
                .toList();
    }

    public void cancel(String id) {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new RegistrationNotFoundException(id));

        if (registration.getStatus() == RegistrationStatus.CANCELLED) {
            throw new IllegalStateException("Registration is already cancelled");
        }

        registration.setStatus(RegistrationStatus.CANCELLED);
        registration.setProcessedAt(LocalDateTime.now());
        registrationRepository.save(registration);

        if (registration.getStatus() == RegistrationStatus.CONFIRMED) {
            eventPublisher.publishRegistrationCancelled(registration);
        }
    }

    private RegistrationResponse toResponse(Registration registration) {
        return RegistrationResponse.builder()
                .id(registration.getId())
                .gameId(registration.getGameId())
                .playerName(registration.getPlayerName())
                .email(registration.getEmail())
                .status(registration.getStatus())
                .requestedAt(registration.getRequestedAt())
                .processedAt(registration.getProcessedAt())
                .build();
    }
}