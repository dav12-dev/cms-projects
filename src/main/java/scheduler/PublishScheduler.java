package com.cms.cms.scheduler;

import com.cms.cms.entity.Article;
import com.cms.cms.repository.ArticleRepository;
import com.cms.cms.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PublishScheduler {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Scheduled(fixedDelay = 60000) // Check every minute
    public void publishScheduledArticles() {
        List<Article> scheduled = articleRepository.findByStatus("SCHEDULED");
        LocalDateTime now = LocalDateTime.now();
        for (Article article : scheduled) {
            if (article.getPublishAt() != null && article.getPublishAt().isBefore(now)) {
                article.setStatus("PUBLISHED");
                articleRepository.save(article);
                auditLogService.log("PUBLISH", "Article", article.getId(), "Automatically published: " + article.getTitle());
                System.out.println("✅ Published scheduled article: " + article.getTitle());
            }
        }
    }
}