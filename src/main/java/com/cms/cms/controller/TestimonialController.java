package com.cms.cms.controller;

import com.cms.cms.entity.Testimonial;
import com.cms.cms.repository.TestimonialRepository;
import com.cms.cms.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/testimonials")
public class TestimonialController {

    @Autowired
    private TestimonialRepository testimonialRepository;

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    public List<Testimonial> getAllTestimonials() {
        return testimonialRepository.findAll();
    }

    @GetMapping("/{id}")
    public Testimonial getTestimonialById(@PathVariable Long id) {
        return testimonialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Testimonial not found"));
    }

    @PostMapping
    public Testimonial createTestimonial(@RequestBody Testimonial testimonial) {
        Testimonial saved = testimonialRepository.save(testimonial);
        auditLogService.log("CREATE", "Testimonial", saved.getId(), "Created testimonial for: " + saved.getClientName());
        return saved;
    }

    @PutMapping("/{id}")
    public Testimonial updateTestimonial(@PathVariable Long id, @RequestBody Testimonial testimonial) {
        Testimonial existing = testimonialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Testimonial not found"));
        existing.setClientName(testimonial.getClientName());
        existing.setClientCompany(testimonial.getClientCompany());
        existing.setContent(testimonial.getContent());
        existing.setRating(testimonial.getRating());
        existing.setStatus(testimonial.getStatus());
        Testimonial updated = testimonialRepository.save(existing);
        auditLogService.log("UPDATE", "Testimonial", id, "Updated testimonial for: " + updated.getClientName());
        return updated;
    }

    @DeleteMapping("/{id}")
    public void deleteTestimonial(@PathVariable Long id) {
        Testimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Testimonial not found"));
        auditLogService.log("DELETE", "Testimonial", id, "Deleted testimonial for: " + testimonial.getClientName());
        testimonialRepository.deleteById(id);
    }
}