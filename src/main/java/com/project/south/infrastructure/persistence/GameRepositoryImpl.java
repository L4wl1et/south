package com.project.south.infrastructure.persistence;

import com.project.south.domain.model.Game;
import com.project.south.domain.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GameRepositoryImpl implements GameRepository {

    private final MongoGameRepository mongoGameRepository;

    @Override
    public Game save(Game game) {
        return mongoGameRepository.save(game);
    }

    @Override
    public Optional<Game> findById(String id) {
        return mongoGameRepository.findById(id);
    }

    @Override
    public List<Game> findAll() {
        return mongoGameRepository.findAll();
    }

    @Override
    public void deleteById(String id) {
        mongoGameRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return mongoGameRepository.existsById(id);
    }
}