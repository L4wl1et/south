package com.project.south.infrastructure.persistence;

import com.project.south.domain.model.Game;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoGameRepository extends MongoRepository<Game, String> {
}