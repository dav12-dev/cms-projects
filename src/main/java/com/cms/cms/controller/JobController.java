package com.cms.cms.controller;

import com.cms.cms.entity.Job;
import com.cms.cms.repository.JobRepository;
import com.cms.cms.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    @GetMapping("/{id}")
    public Job getJobById(@PathVariable Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }

    @PostMapping
    public Job createJob(@RequestBody Job job) {
        Job saved = jobRepository.save(job);
        auditLogService.log("CREATE", "Job", saved.getId(), "Created job: " + saved.getTitle());
        return saved;
    }

    @PutMapping("/{id}")
    public Job updateJob(@PathVariable Long id, @RequestBody Job job) {
        Job existing = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        existing.setTitle(job.getTitle());
        existing.setCompany(job.getCompany());
        existing.setLocation(job.getLocation());
        existing.setType(job.getType());
        existing.setSalary(job.getSalary());
        existing.setDescription(job.getDescription());
        existing.setApplyUrl(job.getApplyUrl());
        existing.setContactEmail(job.getContactEmail());
        existing.setStatus(job.getStatus());
        Job updated = jobRepository.save(existing);
        auditLogService.log("UPDATE", "Job", id, "Updated job: " + updated.getTitle());
        return updated;
    }

    @DeleteMapping("/{id}")
    public void deleteJob(@PathVariable Long id) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        auditLogService.log("DELETE", "Job", id, "Deleted job: " + job.getTitle());
        jobRepository.deleteById(id);
    }
}