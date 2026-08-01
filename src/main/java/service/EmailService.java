package com.cms.cms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendWelcomeEmail(String to, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Welcome to CMS!");
            message.setText("Hello " + name + ",\n\nWelcome to our Content Management System!\n\nYou can now log in and start managing your content.\n\nBest regards,\nThe CMS Team");
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Email not sent: " + e.getMessage());
        }
    }

    public void sendPasswordResetEmail(String to, String name, String resetLink) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Password Reset Request");
            message.setText("Hello " + name + ",\n\nWe received a request to reset your password.\n\nClick the link below to reset your password:\n" + resetLink + "\n\nThis link will expire in 1 hour.\n\nIf you did not request this, please ignore this email.\n\nBest regards,\nThe CMS Team");
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println("Password reset email not sent: " + e.getMessage());
        }
    }
}