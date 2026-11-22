package com.dev.secretproviders.config;

import com.dev.secretproviders.domain.AppUser;
import com.dev.secretproviders.domain.Authorities;
import com.dev.secretproviders.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Creates demo users for local development only. The shared password comes
 * from configuration, so no credentials are committed to the repository.
 */
@Component
@Profile("local")
class DemoUsersInitializer implements CommandLineRunner {

    private static final Set<String> STANDARD_USERS_PERMISSIONS = Set.of(Authorities.STANDARD);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String password;

    DemoUsersInitializer(UserRepository userRepository,
                         PasswordEncoder passwordEncoder,
                         @Value("${app.demo-users.password}") String password) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.password = password;
    }

    @Override
    public void run(String... args) {
        createIfMissing("sender@example.com", STANDARD_USERS_PERMISSIONS);
        createIfMissing("recipient@example.com", STANDARD_USERS_PERMISSIONS);
        createIfMissing("outsider@example.com", Set.of());
    }

    private void createIfMissing(String email, Set<String> permissions) {
        if (!userRepository.existsByEmail(email)) {
            userRepository.save(new AppUser(email, passwordEncoder.encode(password), permissions));
        }
    }

}
