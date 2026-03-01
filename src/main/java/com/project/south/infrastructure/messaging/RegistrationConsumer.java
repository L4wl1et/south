package com.project.south.infrastructure.messaging;

import com.project.south.domain.model.Game;
import com.project.south.domain.model.Registration;
import com.project.south.domain.model.RegistrationStatus;
import com.project.south.domain.repository.GameRepository;
import com.project.south.domain.repository.RegistrationRepository;
import com.project.south.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationConsumer {

    private final RegistrationRepository registrationRepository;
    private final GameRepository gameRepository;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_REQUESTED)
    public void handleRegistrationRequested(Registration registration) {
        log.info("Processing registration requested for registrationId: {}", registration.getId());

        try {
            Registration current = registrationRepository.findById(registration.getId())
                    .orElse(registration);

            gameRepository.findById(current.getGameId()).ifPresentOrElse(game -> {
                if (game.hasAvailableSlots()) {
                    game.setConfirmedSlots(game.getConfirmedSlots() + 1);
                    gameRepository.save(game);
                    updateRegistrationStatus(current, RegistrationStatus.CONFIRMED);
                    log.info("Registration CONFIRMED for registrationId: {}", current.getId());
                } else {
                    updateRegistrationStatus(current, RegistrationStatus.WAITLISTED);
                    log.info("Registration WAITLISTED for registrationId: {}", current.getId());
                }
            }, () -> {
                log.warn("Game not found for registrationId: {}", current.getId());
                updateRegistrationStatus(current, RegistrationStatus.CANCELLED);
            });

        } catch (OptimisticLockingFailureException e) {
            log.warn("Optimistic locking conflict for registrationId: {}. Retrying...", registration.getId());
            throw e;
        }
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_CANCELLED)
    public void handleRegistrationCancelled(Registration registration) {
        log.info("Processing cancellation for registrationId: {}", registration.getId());

        gameRepository.findById(registration.getGameId()).ifPresent(game -> {
            if (game.getConfirmedSlots() > 0) {
                game.setConfirmedSlots(game.getConfirmedSlots() - 1);
                gameRepository.save(game);
            }

            registrationRepository
                    .findFirstByGameIdAndStatus(registration.getGameId(), RegistrationStatus.WAITLISTED)
                    .ifPresent(waitlisted -> {
                        updateRegistrationStatus(waitlisted, RegistrationStatus.CONFIRMED);
                        log.info("Promoted waitlisted registration: {}", waitlisted.getId());
                    });
        });
    }

    private void updateRegistrationStatus(Registration registration, RegistrationStatus status) {
        registration.setStatus(status);
        registration.setProcessedAt(LocalDateTime.now());
        registrationRepository.save(registration);
    }
}