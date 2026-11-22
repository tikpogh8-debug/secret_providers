package com.dev.secretproviders.repository;

import com.dev.secretproviders.domain.Secret;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface SecretRepository extends JpaRepository<Secret, String> {

    /**
     * Locks the row so two concurrent readers cannot both obtain the secret.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Secret> findWithLockByTokenHash(String tokenHash);

}
