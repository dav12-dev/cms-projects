package com.cms.cms.repository;

import com.cms.cms.entity.ArticleVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ArticleVersionRepository extends JpaRepository<ArticleVersion, Long> {
    List<ArticleVersion> findByArticleIdOrderByVersionNumberDesc(Long articleId);
    List<ArticleVersion> findByArticleId(Long articleId);
}