package com.cms.cms.controller;

import com.cms.cms.dto.PasswordResetDto;
import com.cms.cms.dto.ResetRequest;
import com.cms.cms.entity.User;
import com.cms.cms.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // Comment out EmailService for now – it might be failing
    // @Autowired
    // private EmailService emailService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ==============================
    // REGISTER – BULLETPROOF VERSION
    // ==============================
    @PostMapping("/register")
    @Transactional
    public String registerUser(@RequestBody User user) {
        try {
            log.info("🔹 Registration attempt for email: {}", user.getEmail());

            // Validate input
            if (user.getEmail() == null || user.getEmail().isEmpty()) {
                throw new RuntimeException("Email is required");
            }
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                throw new RuntimeException("Password is required");
            }
            if (user.getFullName() == null || user.getFullName().isEmpty()) {
                throw new RuntimeException("Full name is required");
            }

            // Check if email already exists
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new RuntimeException("Email already registered");
            }

            // Encode password
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // Set defaults
            user.setRole(user.getRole() != null ? user.getRole() : "USER");
            user.setActive(true);
            user.setThemePreference(user.getThemePreference() != null ? user.getThemePreference() : "light");

            // Save user
            User savedUser = userRepository.save(user);
            log.info("✅ User saved with ID: {}", savedUser.getId());

            // Return success
            return "Registration successful! Please log in.";

        } catch (Exception e) {
            log.error("❌ Registration failed for email: {}", user.getEmail(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    // ==============================
    // GET ALL USERS
    // ==============================
    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ==============================
    // UPDATE USER ROLE
    // ==============================
    @PutMapping("/{id}/role")
    public User updateUserRole(@PathVariable Long id, @RequestBody String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(role.toUpperCase());
        return userRepository.save(user);
    }

    // ==============================
    // DELETE USER
    // ==============================
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }

    // ==============================
    // UPDATE THEME PREFERENCE
    // ==============================
    @PutMapping("/theme")
    public User updateTheme(@RequestBody Map<String, String> request, Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String theme = request.get("theme");
        if (theme != null && !theme.isEmpty()) {
            user.setThemePreference(theme);
        }
        return userRepository.save(user);
    }

    // ==============================
    // FORGOT PASSWORD – request reset
    // ==============================
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody ResetRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with this email"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        String resetLink = baseUrl + "/reset-password?token=" + token;
        // EmailService commented out – uncomment when ready
        // emailService.sendPasswordResetEmail(user.getEmail(), user.getFullName(), resetLink);

        return "Password reset link has been sent to your email.";
    }

    // ==============================
    // RESET PASSWORD – confirm token
    // ==============================
    @PostMapping("/reset-password")
    public String resetPassword(@RequestBody PasswordResetDto resetDto) {
        User user = userRepository.findByResetToken(resetDto.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token has expired. Please request a new password reset.");
        }

        user.setPassword(passwordEncoder.encode(resetDto.getNewPassword()));
        user.setResetToken(null);
        user.setTokenExpiry(null);
        userRepository.save(user);

        return "Password has been reset successfully!";
    }
}