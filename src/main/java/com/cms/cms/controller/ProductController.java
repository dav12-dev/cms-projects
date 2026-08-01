package com.cms.cms.controller;

import com.cms.cms.entity.Product;
import com.cms.cms.repository.ProductRepository;
import com.cms.cms.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        Product saved = productRepository.save(product);
        auditLogService.log("CREATE", "Product", saved.getId(), "Created product: " + saved.getName());
        return saved;
    }

    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        existing.setQuantity(product.getQuantity());
        existing.setSku(product.getSku());
        existing.setStatus(product.getStatus());
        existing.setCategoryId(product.getCategoryId());
        existing.setImageUrl(product.getImageUrl());
        Product updated = productRepository.save(existing);
        auditLogService.log("UPDATE", "Product", id, "Updated product: " + updated.getName());
        return updated;
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        auditLogService.log("DELETE", "Product", id, "Deleted product: " + product.getName());
        productRepository.deleteById(id);
    }
}