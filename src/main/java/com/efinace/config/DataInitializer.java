package com.efinace.config;

import com.efinace.entity.Role;
import com.efinace.entity.User;
import com.efinace.enums.RoleName;
import com.efinace.enums.UserStatus;
import com.efinace.repository.RoleRepository;
import com.efinace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

/**
 * Seeds a default ADMIN user on first startup if none exists.
 * Credentials come from environment variables (never hardcoded).
 */
@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        // Only seed if no admin user exists yet
        if (userRepository.findByEmail(adminEmail).isPresent()) {
            logger.info("Admin user already exists — skipping seed.");
            return;
        }

        Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseThrow(() -> new RuntimeException("ADMIN role not found in database. Check db.sql seed data."));

        User admin = User.builder()
                .fullName("Daxin Tudu")
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .status(UserStatus.ACTIVE)
                .roles(Set.of(adminRole))
                .build();

        userRepository.save(admin);
        logger.info("Default admin user created: {}", adminEmail);
    }
}
