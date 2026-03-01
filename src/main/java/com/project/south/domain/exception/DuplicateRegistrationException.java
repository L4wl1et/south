package com.project.south.domain.exception;

public class DuplicateRegistrationException extends RuntimeException {
    public DuplicateRegistrationException(String email, String gameId) {
        super("Player " + email + " is already registered for game: " + gameId);
    }
}