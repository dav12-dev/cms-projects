package com.cms.cms.repository;

import com.cms.cms.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query("SELECT a FROM Article a LEFT JOIN FETCH a.category WHERE a.status = :status")
    List<Article> findByStatus(@Param("status") String status);

    Optional<Article> findBySlug(String slug);

    @Query("SELECT a FROM Article a LEFT JOIN FETCH a.category")
    Page<Article> findAllWithCategory(Pageable pageable);

    @Query("SELECT a FROM Article a LEFT JOIN FETCH a.category " +
            "WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Article> searchArticlesWithCategory(@Param("keyword") String keyword, Pageable pageable);

    // Advanced search with multiple filters
    @Query("SELECT DISTINCT a FROM Article a LEFT JOIN FETCH a.category c LEFT JOIN a.tags t " +
            "WHERE (:keyword IS NULL OR LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:categoryId IS NULL OR c.id = :categoryId) " +
            "AND (:tag IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :tag, '%'))) " +
            "AND (:status IS NULL OR a.status = :status)")
    Page<Article> advancedSearch(@Param("keyword") String keyword,
                                 @Param("categoryId") Long categoryId,
                                 @Param("tag") String tag,
                                 @Param("status") String status,
                                 Pageable pageable);

    @Query("SELECT a FROM Article a LEFT JOIN FETCH a.category WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.content) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Article> searchArticlesLegacy(@Param("keyword") String keyword);

    long countByCategoryId(Long categoryId);

    @Modifying
    @Transactional
    @Query("UPDATE Article a SET a.category = NULL WHERE a.category.id = :categoryId")
    void setCategoryToNullForCategory(@Param("categoryId") Long categoryId);

    @Query("SELECT a FROM Article a LEFT JOIN FETCH a.category WHERE a.status = 'PUBLISHED'")
    List<Article> findAllPublished();
}