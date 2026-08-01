package com.cms.cms.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "form_submissions")
public class FormSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String data; // JSON key-value pairs

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @ManyToOne
    @JoinColumn(name = "form_id")
    private Form form;

    public FormSubmission() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public Form getForm() { return form; }
    public void setForm(Form form) { this.form = form; }

    @PrePersist
    protected void onCreate() {
        submittedAt = LocalDateTime.now();
    }
}