package com.project.south.application.service;

import com.project.south.application.dto.GameRequest;
import com.project.south.application.dto.GameResponse;
import com.project.south.domain.exception.GameNotFoundException;
import com.project.south.domain.model.Game;
import com.project.south.domain.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameService gameService;

    private Game game;
    private GameRequest request;

    @BeforeEach
    void setUp() {
        request = new GameRequest();
        request.setTitle("Game Test");
        request.setDescription("Description Test");
        request.setTotalSlots(100);
        request.setRegistrationOpen(true);

        game = Game.builder()
                .id("123")
                .title("Game Test")
                .description("Description Test")
                .totalSlots(100)
                .confirmedSlots(0)
                .registrationOpen(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldCreateGameSuccessfully() {
        when(gameRepository.save(any(Game.class))).thenReturn(game);

        GameResponse response = gameService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Game Test");
        assertThat(response.getTotalSlots()).isEqualTo(100);
        assertThat(response.getConfirmedSlots()).isEqualTo(0);
        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    void shouldFindGameByIdSuccessfully() {
        when(gameRepository.findById("123")).thenReturn(Optional.of(game));

        GameResponse response = gameService.findById("123");

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("123");
    }

    @Test
    void shouldThrowGameNotFoundExceptionWhenGameDoesNotExist() {
        when(gameRepository.findById("invalid")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gameService.findById("invalid"))
                .isInstanceOf(GameNotFoundException.class)
                .hasMessageContaining("invalid");
    }

    @Test
    void shouldReturnAllGames() {
        when(gameRepository.findAll()).thenReturn(List.of(game));

        List<GameResponse> responses = gameService.findAll();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getTitle()).isEqualTo("Game Test");
    }

    @Test
    void shouldUpdateGameSuccessfully() {
        when(gameRepository.findById("123")).thenReturn(Optional.of(game));
        when(gameRepository.save(any(Game.class))).thenReturn(game);

        GameResponse response = gameService.update("123", request);

        assertThat(response).isNotNull();
        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    void shouldDeleteGameSuccessfully() {
        when(gameRepository.existsById("123")).thenReturn(true);
        doNothing().when(gameRepository).deleteById("123");

        assertThatCode(() -> gameService.delete("123")).doesNotThrowAnyException();
        verify(gameRepository, times(1)).deleteById("123");
    }

    @Test
    void shouldThrowGameNotFoundExceptionWhenDeletingNonExistentGame() {
        when(gameRepository.existsById("invalid")).thenReturn(false);

        assertThatThrownBy(() -> gameService.delete("invalid"))
                .isInstanceOf(GameNotFoundException.class);
    }
}