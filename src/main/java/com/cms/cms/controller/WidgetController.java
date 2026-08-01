package com.cms.cms.controller;

import com.cms.cms.entity.Article;
import com.cms.cms.entity.Category;
import com.cms.cms.repository.ArticleRepository;
import com.cms.cms.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/widgets")
public class WidgetController {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/recent-posts")
    public List<Article> getRecentPosts() {
        return articleRepository.findByStatus("PUBLISHED")
                .stream().limit(5).toList();
    }

    @GetMapping("/popular-posts")
    public List<Article> getPopularPosts() {
        return articleRepository.findAllPublished()
                .stream()
                .sorted((a, b) -> Integer.compare(b.getLikes(), a.getLikes()))
                .limit(5)
                .toList();
    }

    @GetMapping("/categories")
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping("/stats")
    public Map<String, Long> getStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("articles", (long) articleRepository.findAllPublished().size());
        stats.put("categories", categoryRepository.count());
        return stats;
    }
}