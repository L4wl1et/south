package com.project.south.domain.exception;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(String id) {
        super("Game not found with id: " + id);
    }
}