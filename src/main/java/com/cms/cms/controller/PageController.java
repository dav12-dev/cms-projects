package com.cms.cms.controller;

import com.cms.cms.entity.Page;
import com.cms.cms.repository.PageRepository;
import com.cms.cms.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pages")
public class PageController {
    @Autowired private PageRepository pageRepository;
    @Autowired private AuditLogService auditLogService;

    @GetMapping public List<Page> getAllPages() { return pageRepository.findAll(); }
    @GetMapping("/{slug}") public Page getPageBySlug(@PathVariable String slug) { return pageRepository.findBySlug(slug).orElseThrow(); }
    @PostMapping public Page createPage(@RequestBody Page page) { Page saved = pageRepository.save(page); auditLogService.log("CREATE", "Page", saved.getId(), "Created page: " + saved.getTitle()); return saved; }
    @PutMapping("/{id}") public Page updatePage(@PathVariable Long id, @RequestBody Page page) { Page existing = pageRepository.findById(id).orElseThrow(); existing.setTitle(page.getTitle()); existing.setContent(page.getContent()); existing.setStatus(page.getStatus()); existing.setMetaTitle(page.getMetaTitle()); existing.setMetaDescription(page.getMetaDescription()); Page updated = pageRepository.save(existing); auditLogService.log("UPDATE", "Page", id, "Updated page: " + updated.getTitle()); return updated; }
    @DeleteMapping("/{id}") public void deletePage(@PathVariable Long id) { Page page = pageRepository.findById(id).orElseThrow(); auditLogService.log("DELETE", "Page", id, "Deleted page: " + page.getTitle()); pageRepository.deleteById(id); }
}