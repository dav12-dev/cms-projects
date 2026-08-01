package com.cms.cms.controller;

import com.cms.cms.entity.News;
import com.cms.cms.repository.NewsRepository;
import com.cms.cms.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    public List<News> getAllNews() {
        return newsRepository.findAll();
    }

    @GetMapping("/breaking")
    public List<News> getBreakingNews() {
        return newsRepository.findByIsBreakingTrue();
    }

    @GetMapping("/{id}")
    public News getNewsById(@PathVariable Long id) {
        return newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found"));
    }

    @PostMapping
    public News createNews(@RequestBody News news) {
        News saved = newsRepository.save(news);
        auditLogService.log("CREATE", "News", saved.getId(), "Created news: " + saved.getTitle());
        return saved;
    }

    @PutMapping("/{id}")
    public News updateNews(@PathVariable Long id, @RequestBody News news) {
        News existing = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found"));
        existing.setTitle(news.getTitle());
        existing.setContent(news.getContent());
        existing.setCategory(news.getCategory());
        existing.setAuthor(news.getAuthor());
        existing.setPublishDate(news.getPublishDate());
        existing.setBreaking(news.isBreaking());
        existing.setStatus(news.getStatus());
        News updated = newsRepository.save(existing);
        auditLogService.log("UPDATE", "News", id, "Updated news: " + updated.getTitle());
        return updated;
    }

    @DeleteMapping("/{id}")
    public void deleteNews(@PathVariable Long id) {
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("News not found"));
        auditLogService.log("DELETE", "News", id, "Deleted news: " + news.getTitle());
        newsRepository.deleteById(id);
    }
}