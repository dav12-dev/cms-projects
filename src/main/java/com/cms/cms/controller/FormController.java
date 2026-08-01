package com.cms.cms.controller;

import com.cms.cms.entity.Form;
import com.cms.cms.entity.FormSubmission;
import com.cms.cms.repository.FormRepository;
import com.cms.cms.repository.FormSubmissionRepository;
import com.cms.cms.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forms")
public class FormController {

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private FormSubmissionRepository submissionRepository;

    @Autowired
    private AuditLogService auditLogService;

    // Form CRUD
    @GetMapping
    public List<Form> getAllForms() {
        return formRepository.findAll();
    }

    @GetMapping("/{id}")
    public Form getFormById(@PathVariable Long id) {
        return formRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Form not found"));
    }

    @PostMapping
    public Form createForm(@RequestBody Form form) {
        Form saved = formRepository.save(form);
        auditLogService.log("CREATE", "Form", saved.getId(), "Created form: " + saved.getTitle());
        return saved;
    }

    @PutMapping("/{id}")
    public Form updateForm(@PathVariable Long id, @RequestBody Form form) {
        Form existing = formRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Form not found"));
        existing.setTitle(form.getTitle());
        existing.setDescription(form.getDescription());
        existing.setFields(form.getFields());
        existing.setStatus(form.getStatus());
        Form updated = formRepository.save(existing);
        auditLogService.log("UPDATE", "Form", id, "Updated form: " + updated.getTitle());
        return updated;
    }

    @DeleteMapping("/{id}")
    public void deleteForm(@PathVariable Long id) {
        Form form = formRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Form not found"));
        auditLogService.log("DELETE", "Form", id, "Deleted form: " + form.getTitle());
        formRepository.deleteById(id);
    }

    // Form Submissions
    @GetMapping("/{formId}/submissions")
    public List<FormSubmission> getSubmissions(@PathVariable Long formId) {
        return submissionRepository.findByFormId(formId);
    }

    @PostMapping("/{formId}/submissions")
    public FormSubmission submitForm(@PathVariable Long formId, @RequestBody String data) {
        Form form = formRepository.findById(formId)
                .orElseThrow(() -> new RuntimeException("Form not found"));
        FormSubmission submission = new FormSubmission();
        submission.setData(data);
        submission.setForm(form);
        return submissionRepository.save(submission);
    }

    @DeleteMapping("/submissions/{id}")
    public void deleteSubmission(@PathVariable Long id) {
        submissionRepository.deleteById(id);
    }
}