package com.cms.cms.service;

import com.cms.cms.entity.Article;
import com.cms.cms.entity.ArticleVersion;
import com.cms.cms.repository.ArticleRepository;
import com.cms.cms.repository.ArticleVersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ArticleVersionService {

    @Autowired
    private ArticleVersionRepository versionRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private AuditLogService auditLogService;

    public void saveVersion(Article article, String changeComment) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth != null ? auth.getName() : "system";

        // Get the latest version number
        List<ArticleVersion> existing = versionRepository.findByArticleId(article.getId());
        int nextVersion = existing.isEmpty() ? 1 : existing.size() + 1;

        ArticleVersion version = article.createVersion(username, changeComment);
        version.setVersionNumber(nextVersion);
        versionRepository.save(version);

        auditLogService.log("VERSION", "Article", article.getId(),
                "Created version " + nextVersion + " for article: " + article.getTitle());
    }

    public List<ArticleVersion> getVersionsByArticleId(Long articleId) {
        return versionRepository.findByArticleIdOrderByVersionNumberDesc(articleId);
    }

    public ArticleVersion getVersionById(Long versionId) {
        return versionRepository.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Version not found"));
    }

    @Transactional
    public Article rollbackToVersion(Long articleId, int versionNumber) {
        List<ArticleVersion> versions = versionRepository.findByArticleId(articleId);
        ArticleVersion targetVersion = versions.stream()
                .filter(v -> v.getVersionNumber() == versionNumber)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Version not found"));

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        // Save current state as a new version before rollback
        saveVersion(article, "Rolled back to version " + versionNumber);

        // Restore the article from the version
        article.setTitle(targetVersion.getTitle());
        article.setContent(targetVersion.getContent());
        article.setStatus(targetVersion.getStatus());
        article.setAuthorName(targetVersion.getAuthorName());
        article.setMetaTitle(targetVersion.getMetaTitle());
        article.setMetaDescription(targetVersion.getMetaDescription());

        Article restored = articleRepository.save(article);
        auditLogService.log("ROLLBACK", "Article", articleId,
                "Rolled back to version " + versionNumber + " for article: " + article.getTitle());

        return restored;
    }

    public int getLatestVersionNumber(Long articleId) {
        List<ArticleVersion> versions = versionRepository.findByArticleId(articleId);
        return versions.isEmpty() ? 0 : versions.size();
    }

    public void deleteVersionsForArticle(Long articleId) {
        List<ArticleVersion> versions = versionRepository.findByArticleId(articleId);
        versionRepository.deleteAll(versions);
    }
}