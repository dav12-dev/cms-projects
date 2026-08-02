package com.cms.cms.service;

import com.cms.cms.entity.User;  // ← CORRECT import
import com.cms.cms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(User user) {
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set default values
        user.setActive(true);
        if (user.getRole() == null) {
            user.setRole("ROLE_USER");
        }

        log.info("Saving user with email: {}", user.getEmail());
        User saved = userRepository.save(user);
        log.info("User saved with ID: {}", saved.getId());
        return saved;
    }

    // Optional: find by email
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    // Optional: check if user exists
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}