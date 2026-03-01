package com.project.south.domain.exception;

public class RegistrationNotFoundException extends RuntimeException {
    public RegistrationNotFoundException(String id) {
        super("Registration not found with id: " + id);
    }
}