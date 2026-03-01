package com.project.south.infrastructure.messaging;

import com.project.south.domain.model.Registration;
import com.project.south.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQRegistrationEventPublisher implements RegistrationEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publishRegistrationRequested(Registration registration) {
        log.info("Publishing registration requested event for registrationId: {}", registration.getId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_REQUESTED,
                registration
        );
    }

    @Override
    public void publishRegistrationCancelled(Registration registration) {
        log.info("Publishing registration cancelled event for registrationId: {}", registration.getId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_CANCELLED,
                registration
        );
    }
}