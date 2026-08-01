package com.cms.cms.controller;

import com.cms.cms.entity.Article;
import com.cms.cms.entity.Category;
import com.cms.cms.entity.Tag;
import com.cms.cms.entity.ArticleVersion;
import com.cms.cms.repository.ArticleRepository;
import com.cms.cms.repository.CategoryRepository;
import com.cms.cms.repository.TagRepository;
import com.cms.cms.service.ArticleVersionService;
import com.cms.cms.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private ArticleVersionService versionService;

    // ---------- Basic Endpoints ----------
    @GetMapping
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    @GetMapping("/page")
    public Page<Article> getArticlesPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return articleRepository.findAllWithCategory(pageable);
    }

    // ---------- Simple Search ----------
    @GetMapping("/search")
    public Page<Article> searchArticles(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return articleRepository.searchArticlesWithCategory(keyword.trim(), pageable);
    }

    // ---------- Advanced Search ----------
    @GetMapping("/advanced-search")
    public Page<Article> advancedSearch(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if ((keyword == null || keyword.trim().isEmpty()) &&
                categoryId == null && tag == null && status == null) {
            return articleRepository.findAllWithCategory(pageable);
        }

        return articleRepository.advancedSearch(keyword, categoryId, tag, status, pageable);
    }

    // ---------- CRUD ----------
    @PostMapping
    public Article createArticle(@RequestBody Article article) {
        if (article.getCategory() != null && article.getCategory().getId() != null) {
            Category category = categoryRepository.findById(article.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            article.setCategory(category);
        }
        if (article.getStatus() == null || article.getStatus().isEmpty()) {
            article.setStatus("DRAFT");
        }
        if (article.getSlug() == null || article.getSlug().isEmpty()) {
            article.setSlug(article.generateSlug(article.getTitle()));
        }
        String baseSlug = article.getSlug();
        int counter = 1;
        while (articleRepository.findBySlug(article.getSlug()).isPresent()) {
            article.setSlug(baseSlug + "-" + counter);
            counter++;
        }

        if (article.getTags() != null && !article.getTags().isEmpty()) {
            List<Tag> processedTags = article.getTags().stream()
                    .map(tagObj -> tagRepository.findByName(tagObj.getName())
                            .orElseGet(() -> tagRepository.save(new Tag(tagObj.getName()))))
                    .toList();
            article.setTags(processedTags);
        }

        Article saved = articleRepository.save(article);
        auditLogService.log("CREATE", "Article", saved.getId(), "Created article: " + saved.getTitle());

        // Save initial version
        versionService.saveVersion(saved, "Initial version");

        return saved;
    }

    @GetMapping("/{id}")
    public Article getArticleById(@PathVariable Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));
    }

    @PutMapping("/{id}")
    public Article updateArticle(@PathVariable Long id, @RequestBody Article article) {
        Article existing = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        // Save version before update
        versionService.saveVersion(existing, "Updated article: " + article.getTitle());

        existing.setTitle(article.getTitle());
        existing.setContent(article.getContent());
        existing.setStatus(article.getStatus());
        existing.setLikes(article.getLikes());
        existing.setPublishAt(article.getPublishAt());
        existing.setMetaTitle(article.getMetaTitle());
        existing.setMetaDescription(article.getMetaDescription());

        if (!existing.getTitle().equals(article.getTitle())) {
            existing.setSlug(existing.generateSlug(article.getTitle()));
        }

        if (article.getCategory() != null && article.getCategory().getId() != null) {
            Category category = categoryRepository.findById(article.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            existing.setCategory(category);
        }

        if (article.getTags() != null) {
            List<Tag> processedTags = article.getTags().stream()
                    .map(tagObj -> tagRepository.findByName(tagObj.getName())
                            .orElseGet(() -> tagRepository.save(new Tag(tagObj.getName()))))
                    .toList();
            existing.setTags(processedTags);
        }

        Article updated = articleRepository.save(existing);
        auditLogService.log("UPDATE", "Article", id, "Updated article: " + updated.getTitle());
        return updated;
    }

    @PutMapping("/{id}/like")
    public Article likeArticle(@PathVariable Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));
        article.setLikes(article.getLikes() + 1);
        return articleRepository.save(article);
    }

    @DeleteMapping("/{id}")
    public void deleteArticle(@PathVariable Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));
        auditLogService.log("DELETE", "Article", id, "Deleted article: " + article.getTitle());

        // Delete versions before deleting the article
        versionService.deleteVersionsForArticle(id);
        articleRepository.deleteById(id);
    }

    // ---------- Version History Endpoints ----------

    @GetMapping("/{id}/versions")
    public List<ArticleVersion> getArticleVersions(@PathVariable Long id) {
        return versionService.getVersionsByArticleId(id);
    }

    @PostMapping("/{id}/rollback/{versionNumber}")
    public Article rollbackToVersion(@PathVariable Long id, @PathVariable int versionNumber) {
        return versionService.rollbackToVersion(id, versionNumber);
    }

    @GetMapping("/version/{versionId}")
    public ArticleVersion getVersionById(@PathVariable Long versionId) {
        return versionService.getVersionById(versionId);
    }
}