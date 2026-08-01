package com.cms.cms.controller;

import com.cms.cms.entity.Article;
import com.cms.cms.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PreviewController {

    @Autowired
    private ArticleRepository articleRepository;

    @GetMapping("/article/preview/{id}")
    public String previewArticle(@PathVariable Long id, Model model) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        model.addAttribute("article", article);
        model.addAttribute("isPreview", true);
        return "preview";
    }

    @GetMapping("/article/{slug}")
    public String viewPublishedArticle(@PathVariable String slug, Model model) {
        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        // Only show published articles
        if (!"PUBLISHED".equals(article.getStatus())) {
            throw new RuntimeException("Article not published");
        }

        // Increment view count
        article.incrementViewCount();
        articleRepository.save(article);

        model.addAttribute("article", article);
        model.addAttribute("isPreview", false);
        return "preview";
    }
}