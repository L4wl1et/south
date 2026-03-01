package com.project.south.application.service;

import com.project.south.application.dto.GameRequest;
import com.project.south.application.dto.GameResponse;
import com.project.south.domain.exception.GameNotFoundException;
import com.project.south.domain.model.Game;
import com.project.south.domain.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;

    public GameResponse create(GameRequest request) {
        Game game = Game.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .totalSlots(request.getTotalSlots())
                .confirmedSlots(0)
                .registrationOpen(request.getRegistrationOpen())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return toResponse(gameRepository.save(game));
    }

    public GameResponse findById(String id) {
        return gameRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new GameNotFoundException(id));
    }

    public List<GameResponse> findAll() {
        return gameRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public GameResponse update(String id, GameRequest request) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new GameNotFoundException(id));

        game.setTitle(request.getTitle());
        game.setDescription(request.getDescription());
        game.setTotalSlots(request.getTotalSlots());
        game.setRegistrationOpen(request.getRegistrationOpen());
        game.setUpdatedAt(LocalDateTime.now());

        return toResponse(gameRepository.save(game));
    }

    public void delete(String id) {
        if (!gameRepository.existsById(id)) {
            throw new GameNotFoundException(id);
        }
        gameRepository.deleteById(id);
    }

    private GameResponse toResponse(Game game) {
        return GameResponse.builder()
                .id(game.getId())
                .title(game.getTitle())
                .description(game.getDescription())
                .totalSlots(game.getTotalSlots())
                .confirmedSlots(game.getConfirmedSlots())
                .availableSlots(game.getAvailableSlots())
                .registrationOpen(game.isRegistrationOpen())
                .createdAt(game.getCreatedAt())
                .updatedAt(game.getUpdatedAt())
                .build();
    }
}