package com.dev.secretproviders.service;

import com.dev.secretproviders.repository.SecretRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class SecretServiceTest {

    @Autowired
    private SecretService secretService;

    @Autowired
    private SecretRepository secretRepository;

    @Autowired
    private TokenService tokenService;

    @Test
    void revealReturnsContentAndDeletesSecret() {
        String token = secretService.store("top secret", "sender@example.com");

        assertThat(secretService.reveal(token)).isEqualTo("top secret");
        assertThat(secretService.exists(token)).isFalse();
    }

    @Test
    void secondRevealFails() {
        String token = secretService.store("top secret", "sender@example.com");
        secretService.reveal(token);

        assertThatThrownBy(() -> secretService.reveal(token))
                .isInstanceOf(SecretNotFoundException.class);
    }

    @Test
    void unknownTokenFails() {
        assertThatThrownBy(() -> secretService.reveal("unknown"))
                .isInstanceOf(SecretNotFoundException.class);
    }

    @Test
    void tokenItselfIsNeverStored() {
        String token = secretService.store("top secret", "sender@example.com");

        assertThat(secretRepository.existsById(token)).isFalse();
        assertThat(secretRepository.existsById(tokenService.hash(token))).isTrue();
    }

}
