package com.cms.cms.controller;

import com.cms.cms.dto.PasswordResetDto;
import com.cms.cms.dto.ResetRequest;
import com.cms.cms.entity.User;
import com.cms.cms.repository.UserRepository;
import com.cms.cms.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/register")
    public String registerUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }
        user.setActive(true);
        if (user.getThemePreference() == null || user.getThemePreference().isEmpty()) {
            user.setThemePreference("light");
        }
        userRepository.save(user);

        emailService.sendWelcomeEmail(user.getEmail(), user.getFullName());
        return "User registered successfully!";
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PutMapping("/{id}/role")
    public User updateUserRole(@PathVariable Long id, @RequestBody String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(role.toUpperCase());
        return userRepository.save(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }

    // ---------- Theme Preference Endpoint ----------
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

    // ---------- Password Reset Endpoints ----------
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody ResetRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with this email"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        String resetLink = baseUrl + "/reset-password?token=" + token;
        emailService.sendPasswordResetEmail(user.getEmail(), user.getFullName(), resetLink);

        return "Password reset link has been sent to your email.";
    }

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