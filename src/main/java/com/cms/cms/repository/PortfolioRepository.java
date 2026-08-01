package com.cms.cms.repository;

import com.cms.cms.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    List<Portfolio> findByStatus(String status);
    List<Portfolio> findByCategory(String category);
}