package com.dev.secretproviders.service;

import com.dev.secretproviders.domain.Secret;
import com.dev.secretproviders.repository.SecretRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
public class SecretService {

    private final SecretRepository secretRepository;
    private final TokenService tokenService;
    private final Clock clock;

    public SecretService(SecretRepository secretRepository, TokenService tokenService, Clock clock) {
        this.secretRepository = secretRepository;
        this.tokenService = tokenService;
        this.clock = clock;
    }

    /**
     * Stores the secret and returns the one-time access token.
     */
    @Transactional
    public String store(String content, String senderEmail) {
        String token = tokenService.generate();
        secretRepository.save(
                new Secret(tokenService.hash(token), content, senderEmail, Instant.now(clock)));
        return token;
    }

    @Transactional(readOnly = true)
    public boolean exists(String token) {
        return secretRepository.existsById(tokenService.hash(token));
    }

    /**
     * Returns the secret content and deletes it in the same transaction.
     */
    @Transactional
    public String reveal(String token) {
        Secret secret = secretRepository.findWithLockByTokenHash(tokenService.hash(token))
                .orElseThrow(SecretNotFoundException::new);
        secretRepository.delete(secret);
        return secret.getContent();
    }

}
