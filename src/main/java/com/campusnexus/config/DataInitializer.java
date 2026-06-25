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
    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder, org.springframework.jdbc.core.JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Automatically fix legacy database constraint issues on timetables table
        try {
            jdbcTemplate.execute("ALTER TABLE timetables ALTER COLUMN batch_id DROP NOT NULL");
            jdbcTemplate.execute("ALTER TABLE timetables ALTER COLUMN from_time DROP NOT NULL");
            jdbcTemplate.execute("ALTER TABLE timetables ALTER COLUMN to_time DROP NOT NULL");
            jdbcTemplate.execute("ALTER TABLE timetables ALTER COLUMN start_time DROP NOT NULL");
            jdbcTemplate.execute("ALTER TABLE timetables ALTER COLUMN end_time DROP NOT NULL");
            logger.info("Successfully dropped NOT NULL constraints on timetables columns");
            
            // Migrate student years to numeric format
            jdbcTemplate.execute("UPDATE timetables SET year = '1' WHERE year = 'FE'");
            jdbcTemplate.execute("UPDATE timetables SET year = '2' WHERE year = 'SE'");
            jdbcTemplate.execute("UPDATE timetables SET year = '3' WHERE year = 'TE'");
            jdbcTemplate.execute("UPDATE timetables SET year = '4' WHERE year = 'BE'");
            logger.info("Successfully migrated year codes to numbers on timetables table");
        } catch (Exception e) {
            logger.warn("Could not execute startup migrations / alter constraints: " + e.getMessage());
        }

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
