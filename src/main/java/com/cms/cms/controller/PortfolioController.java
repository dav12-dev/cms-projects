package com.cms.cms.controller;

import com.cms.cms.entity.Portfolio;
import com.cms.cms.repository.PortfolioRepository;
import com.cms.cms.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/portfolios")
public class PortfolioController {

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    public List<Portfolio> getAllPortfolios() {
        return portfolioRepository.findAll();
    }

    @GetMapping("/{id}")
    public Portfolio getPortfolioById(@PathVariable Long id) {
        return portfolioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));
    }

    @PostMapping
    public Portfolio createPortfolio(@RequestBody Portfolio portfolio) {
        Portfolio saved = portfolioRepository.save(portfolio);
        auditLogService.log("CREATE", "Portfolio", saved.getId(), "Created portfolio: " + saved.getTitle());
        return saved;
    }

    @PutMapping("/{id}")
    public Portfolio updatePortfolio(@PathVariable Long id, @RequestBody Portfolio portfolio) {
        Portfolio existing = portfolioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));
        existing.setTitle(portfolio.getTitle());
        existing.setDescription(portfolio.getDescription());
        existing.setClient(portfolio.getClient());
        existing.setCategory(portfolio.getCategory());
        existing.setSkills(portfolio.getSkills());
        existing.setProjectUrl(portfolio.getProjectUrl());
        existing.setImageUrl(portfolio.getImageUrl());
        existing.setStatus(portfolio.getStatus());
        Portfolio updated = portfolioRepository.save(existing);
        auditLogService.log("UPDATE", "Portfolio", id, "Updated portfolio: " + updated.getTitle());
        return updated;
    }

    @DeleteMapping("/{id}")
    public void deletePortfolio(@PathVariable Long id) {
        Portfolio portfolio = portfolioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));
        auditLogService.log("DELETE", "Portfolio", id, "Deleted portfolio: " + portfolio.getTitle());
        portfolioRepository.deleteById(id);
    }
}