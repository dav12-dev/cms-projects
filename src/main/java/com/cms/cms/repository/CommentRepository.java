package com.cms.cms.repository;

import com.cms.cms.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByArticleIdAndStatus(Long articleId, String status);
    List<Comment> findByStatus(String status);
    List<Comment> findByArticleId(Long articleId);

    // Get top-level comments (no parent)
    @Query("SELECT c FROM Comment c WHERE c.article.id = :articleId AND c.parent IS NULL AND c.status = 'APPROVED' ORDER BY c.createdAt DESC")
    List<Comment> findTopLevelCommentsByArticleId(@Param("articleId") Long articleId);

    // Get replies for a specific parent comment
    @Query("SELECT c FROM Comment c WHERE c.parent.id = :parentId AND c.status = 'APPROVED' ORDER BY c.createdAt ASC")
    List<Comment> findRepliesByParentId(@Param("parentId") Long parentId);
}