package com.project.south.domain.repository;

import com.project.south.domain.model.Game;

import java.util.List;
import java.util.Optional;

public interface GameRepository {
    Game save(Game game);
    Optional<Game> findById(String id);
    List<Game> findAll();
    void deleteById(String id);
    boolean existsById(String id);
}