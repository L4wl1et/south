package com.project.south.infrastructure.persistence;

import com.project.south.domain.model.Registration;
import com.project.south.domain.model.RegistrationStatus;
import com.project.south.domain.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RegistrationRepositoryImpl implements RegistrationRepository {

    private final MongoRegistrationRepository mongoRegistrationRepository;

    @Override
    public Registration save(Registration registration) {
        return mongoRegistrationRepository.save(registration);
    }

    @Override
    public Optional<Registration> findById(String id) {
        return mongoRegistrationRepository.findById(id);
    }

    @Override
    public List<Registration> findByGameId(String gameId) {
        return mongoRegistrationRepository.findByGameId(gameId);
    }

    @Override
    public List<Registration> findByGameIdAndStatus(String gameId, RegistrationStatus status) {
        return mongoRegistrationRepository.findByGameIdAndStatus(gameId, status);
    }

    @Override
    public Optional<Registration> findFirstByGameIdAndStatus(String gameId, RegistrationStatus status) {
        return mongoRegistrationRepository.findFirstByGameIdAndStatusOrderByRequestedAtAsc(gameId, status);
    }

    @Override
    public void deleteById(String id) {
        mongoRegistrationRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmailAndGameId(String email, String gameId) {
        return mongoRegistrationRepository.existsByEmailAndGameId(email, gameId);
    }
}