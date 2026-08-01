package com.cms.cms.repository;

import com.cms.cms.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FaqRepository extends JpaRepository<Faq, Long> {
    List<Faq> findByStatus(String status);
    List<Faq> findByCategory(String category);
}