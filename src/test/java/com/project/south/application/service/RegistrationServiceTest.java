package com.project.south.application.service;

import com.project.south.application.dto.RegistrationRequest;
import com.project.south.application.dto.RegistrationResponse;
import com.project.south.domain.exception.*;
import com.project.south.domain.model.Game;
import com.project.south.domain.model.Registration;
import com.project.south.domain.model.RegistrationStatus;
import com.project.south.domain.repository.GameRepository;
import com.project.south.domain.repository.RegistrationRepository;
import com.project.south.infrastructure.messaging.RegistrationEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private RegistrationEventPublisher eventPublisher;

    @InjectMocks
    private RegistrationService registrationService;

    private Game game;
    private Registration registration;
    private RegistrationRequest request;

    @BeforeEach
    void setUp() {
        request = new RegistrationRequest();
        request.setPlayerName("Fylip");
        request.setEmail("fylip@email.com");

        game = Game.builder()
                .id("game123")
                .title("Game Test")
                .totalSlots(100)
                .confirmedSlots(0)
                .registrationOpen(true)
                .build();

        registration = Registration.builder()
                .id("reg123")
                .gameId("game123")
                .playerName("Fylip")
                .email("fylip@email.com")
                .status(RegistrationStatus.PENDING)
                .requestedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldRegisterSuccessfully() {
        when(gameRepository.findById("game123")).thenReturn(Optional.of(game));
        when(registrationRepository.existsByEmailAndGameId(any(), any())).thenReturn(false);
        when(registrationRepository.save(any())).thenReturn(registration);

        RegistrationResponse response = registrationService.register("game123", request);

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(RegistrationStatus.PENDING);
        verify(eventPublisher, times(1)).publishRegistrationRequested(any());
    }

    @Test
    void shouldThrowGameNotFoundExceptionWhenGameDoesNotExist() {
        when(gameRepository.findById("invalid")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> registrationService.register("invalid", request))
                .isInstanceOf(GameNotFoundException.class);
    }

    @Test
    void shouldThrowRegistrationClosedExceptionWhenRegistrationIsClosed() {
        game.setRegistrationOpen(false);
        when(gameRepository.findById("game123")).thenReturn(Optional.of(game));

        assertThatThrownBy(() -> registrationService.register("game123", request))
                .isInstanceOf(RegistrationClosedException.class);
    }

    @Test
    void shouldThrowDuplicateRegistrationExceptionWhenEmailAlreadyRegistered() {
        when(gameRepository.findById("game123")).thenReturn(Optional.of(game));
        when(registrationRepository.existsByEmailAndGameId(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> registrationService.register("game123", request))
                .isInstanceOf(DuplicateRegistrationException.class);
    }

    @Test
    void shouldCancelRegistrationSuccessfully() {
        when(registrationRepository.findById("reg123")).thenReturn(Optional.of(registration));
        when(registrationRepository.save(any())).thenReturn(registration);

        assertThatCode(() -> registrationService.cancel("reg123")).doesNotThrowAnyException();
        verify(registrationRepository, times(1)).save(any());
    }

    @Test
    void shouldThrowIllegalStateExceptionWhenCancellingAlreadyCancelledRegistration() {
        registration.setStatus(RegistrationStatus.CANCELLED);
        when(registrationRepository.findById("reg123")).thenReturn(Optional.of(registration));

        assertThatThrownBy(() -> registrationService.cancel("reg123"))
                .isInstanceOf(IllegalStateException.class);
    }
}