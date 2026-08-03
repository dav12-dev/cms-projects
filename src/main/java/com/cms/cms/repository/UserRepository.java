package com.cms.cms.repository;

import com.cms.cms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by email (used for login and password reset)
    Optional<User> findByEmail(String email);

    // Find user by reset token (used for password reset confirmation)
    Optional<User> findByResetToken(String resetToken);

    // NEW: Check if an email already exists in the database (used for registration validation)
    boolean existsByEmail(String email);
}