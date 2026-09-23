package com.team.tasktracker.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.team.tasktracker.user.Role;
import com.team.tasktracker.user.User;
import com.team.tasktracker.user.UserRepository;

/** Creates the first SUPPORT account when the database has no users yet. */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String username;
    private final String password;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder,
            @Value("${app.seed.support-username}") String username,
            @Value("${app.seed.support-password}") String password) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }
        userRepository.save(new User(username, passwordEncoder.encode(password), "Support", Role.SUPPORT));
        log.info("Created initial SUPPORT user '{}'. Change its password after the first login.", username);
    }
}
