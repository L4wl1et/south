package com.project.south.infrastructure.messaging;

import com.project.south.domain.model.Registration;

public interface RegistrationEventPublisher {
    void publishRegistrationRequested(Registration registration);
    void publishRegistrationCancelled(Registration registration);
}