package com.cms.cms.controller;

import com.cms.cms.entity.Faq;
import com.cms.cms.repository.FaqRepository;
import com.cms.cms.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faqs")
public class FaqController {

    @Autowired
    private FaqRepository faqRepository;

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    public List<Faq> getAllFaqs() {
        return faqRepository.findAll();
    }

    @GetMapping("/{id}")
    public Faq getFaqById(@PathVariable Long id) {
        return faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ not found"));
    }

    @PostMapping
    public Faq createFaq(@RequestBody Faq faq) {
        Faq saved = faqRepository.save(faq);
        auditLogService.log("CREATE", "Faq", saved.getId(), "Created FAQ: " + saved.getQuestion());
        return saved;
    }

    @PutMapping("/{id}")
    public Faq updateFaq(@PathVariable Long id, @RequestBody Faq faq) {
        Faq existing = faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ not found"));
        existing.setQuestion(faq.getQuestion());
        existing.setAnswer(faq.getAnswer());
        existing.setCategory(faq.getCategory());
        existing.setOrderIndex(faq.getOrderIndex());
        existing.setStatus(faq.getStatus());
        Faq updated = faqRepository.save(existing);
        auditLogService.log("UPDATE", "Faq", id, "Updated FAQ: " + updated.getQuestion());
        return updated;
    }

    @DeleteMapping("/{id}")
    public void deleteFaq(@PathVariable Long id) {
        Faq faq = faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ not found"));
        auditLogService.log("DELETE", "Faq", id, "Deleted FAQ: " + faq.getQuestion());
        faqRepository.deleteById(id);
    }
}