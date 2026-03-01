package com.project.south.domain.repository;

import com.project.south.domain.model.Registration;
import com.project.south.domain.model.RegistrationStatus;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository {
    Registration save(Registration registration);
    Optional<Registration> findById(String id);
    List<Registration> findByGameId(String gameId);
    List<Registration> findByGameIdAndStatus(String gameId, RegistrationStatus status);
    Optional<Registration> findFirstByGameIdAndStatus(String gameId, RegistrationStatus status);
    void deleteById(String id);
    boolean existsByEmailAndGameId(String email, String gameId);
}