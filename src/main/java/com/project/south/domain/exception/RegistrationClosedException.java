package com.project.south.domain.exception;

public class RegistrationClosedException extends RuntimeException {
    public RegistrationClosedException(String gameId) {
        super("Registrations are closed for game: " + gameId);
    }
}