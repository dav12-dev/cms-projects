package com.cms.cms.controller;

import com.cms.cms.entity.User;
import com.cms.cms.repository.UserRepository;
import com.cms.cms.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class UserProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogService auditLogService;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping
    public User getProfile(Authentication auth) {
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PutMapping
    public User updateProfile(@RequestBody User updatedUser, Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(updatedUser.getFullName());
        // Don't update email or password here – separate endpoint for that

        User saved = userRepository.save(user);
        auditLogService.log("UPDATE", "Profile", user.getId(), "Updated profile for: " + user.getEmail());
        return saved;
    }

    @PutMapping("/change-password")
    public Map<String, String> changePassword(
            @RequestBody Map<String, String> request,
            Authentication auth) {

        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        auditLogService.log("CHANGE_PASSWORD", "User", user.getId(), "Changed password for: " + user.getEmail());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password changed successfully");
        return response;
    }

    @PostMapping("/upload-avatar")
    public User uploadAvatar(@RequestParam("file") MultipartFile file, Authentication auth) throws IOException {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String base64Image = "data:" + file.getContentType() + ";base64," +
                Base64.getEncoder().encodeToString(file.getBytes());
        user.setProfilePicture(base64Image);

        User saved = userRepository.save(user);
        auditLogService.log("UPLOAD_AVATAR", "User", user.getId(), "Uploaded avatar for: " + user.getEmail());
        return saved;
    }
}