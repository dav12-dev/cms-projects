package com.cms.cms.repository;

import com.cms.cms.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findByStatus(String status);
    List<News> findByIsBreakingTrue();
    List<News> findByCategory(String category);
}