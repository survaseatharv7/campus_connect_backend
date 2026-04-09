package com.campusnexus.config;

import com.campusnexus.entity.User;
import com.campusnexus.enums.Role;
import com.campusnexus.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        String adminEmail = "admin@campusnexus.com";

        if (userRepository.existsByEmail(adminEmail)) {
            logger.info("Campus Admin already exists");
            return;
        }

        User admin = User.builder()
                .name("Super Admin")
                .email(adminEmail)
                .password(passwordEncoder.encode("Admin@123"))
                .role(Role.CAMPUS_ADMIN)
                .isActive(true)
                .build();

        userRepository.save(admin);
        logger.info("Campus Admin seeded successfully");
    }
}
