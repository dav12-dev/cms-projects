package com.cms.cms.controller;

import com.cms.cms.entity.Article;
import com.cms.cms.entity.Category;
import com.cms.cms.repository.ArticleRepository;
import com.cms.cms.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // Get all articles
    @GetMapping
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    // Get articles by status (PUBLISHED or DRAFT)
    @GetMapping("/status/{status}")
    public List<Article> getArticlesByStatus(@PathVariable String status) {
        return articleRepository.findByStatus(status);
    }

    // NEW: Search articles by keyword
    @GetMapping("/search")
    public List<Article> searchArticles(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return articleRepository.findAll();
        }
        return articleRepository.searchArticles(keyword.trim());
    }

    // Create a new article
    @PostMapping
    public Article createArticle(@RequestBody Article article) {
        if (article.getCategory() != null && article.getCategory().getId() != null) {
            Category category = categoryRepository.findById(article.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            article.setCategory(category);
        }
        return articleRepository.save(article);
    }

    // Get a single article by ID
    @GetMapping("/{id}")
    public Article getArticleById(@PathVariable Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));
    }

    // Update an existing article
    @PutMapping("/{id}")
    public Article updateArticle(@PathVariable Long id, @RequestBody Article article) {
        Article existing = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        existing.setTitle(article.getTitle());
        existing.setContent(article.getContent());
        existing.setStatus(article.getStatus());

        if (article.getCategory() != null && article.getCategory().getId() != null) {
            Category category = categoryRepository.findById(article.getCategory().getId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            existing.setCategory(category);
        }

        return articleRepository.save(existing);
    }

    // Delete an article
    @DeleteMapping("/{id}")
    public void deleteArticle(@PathVariable Long id) {
        articleRepository.deleteById(id);
    }
}