package com.project.south.infrastructure.persistence;

import com.project.south.domain.model.Registration;
import com.project.south.domain.model.RegistrationStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MongoRegistrationRepository extends MongoRepository<Registration, String> {
    List<Registration> findByGameId(String gameId);
    List<Registration> findByGameIdAndStatus(String gameId, RegistrationStatus status);
    Optional<Registration> findFirstByGameIdAndStatusOrderByRequestedAtAsc(String gameId, RegistrationStatus status);
    boolean existsByEmailAndGameId(String email, String gameId);
}