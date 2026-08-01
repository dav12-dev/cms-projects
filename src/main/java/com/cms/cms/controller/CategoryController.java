package com.cms.cms.controller;

import com.cms.cms.entity.Category;
import com.cms.cms.repository.ArticleRepository;
import com.cms.cms.repository.CategoryRepository;
import com.cms.cms.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        // Check if category with same name exists
        if (categoryRepository.findByName(category.getName()).isPresent()) {
            throw new RuntimeException("Category with this name already exists");
        }
        Category saved = categoryRepository.save(category);
        auditLogService.log("CREATE", "Category", saved.getId(), "Created category: " + saved.getName());
        return saved;
    }

    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Long id, @RequestBody Category category) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        existing.setName(category.getName());
        existing.setDescription(category.getDescription());
        Category updated = categoryRepository.save(existing);
        auditLogService.log("UPDATE", "Category", id, "Updated category: " + updated.getName());
        return updated;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        long articleCount = articleRepository.countByCategoryId(id);
        if (articleCount > 0) {
            articleRepository.setCategoryToNullForCategory(id);
            auditLogService.log("DELETE", "Category", id, "Deleted category: " + category.getName() + " (articles reassigned)");
            categoryRepository.deleteById(id);
            return ResponseEntity.ok("Category deleted. " + articleCount + " articles were reassigned.");
        } else {
            auditLogService.log("DELETE", "Category", id, "Deleted category: " + category.getName());
            categoryRepository.deleteById(id);
            return ResponseEntity.ok("Category deleted successfully.");
        }
    }
}