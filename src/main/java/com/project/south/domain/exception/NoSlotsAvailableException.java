package com.project.south.domain.exception;

public class NoSlotsAvailableException extends RuntimeException {
    public NoSlotsAvailableException(String gameId) {
        super("No slots available for game: " + gameId);
    }
}