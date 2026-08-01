package com.cms.cms.controller;

import com.cms.cms.entity.Article;
import com.cms.cms.entity.Comment;
import com.cms.cms.entity.User;
import com.cms.cms.repository.ArticleRepository;
import com.cms.cms.repository.CommentRepository;
import com.cms.cms.repository.UserRepository;
import com.cms.cms.service.AuditLogService;
import com.cms.cms.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private NotificationService notificationService;

    // Get all comments (Admin only)
    @GetMapping
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    // Get pending comments (Admin/Editor)
    @GetMapping("/pending")
    public List<Comment> getPendingComments() {
        return commentRepository.findByStatus("PENDING");
    }

    // Get top-level comments for an article (with replies nested)
    @GetMapping("/article/{articleId}")
    public List<Comment> getCommentsByArticle(@PathVariable Long articleId) {
        return commentRepository.findTopLevelCommentsByArticleId(articleId);
    }

    // Create a comment (top-level)
    @PostMapping("/article/{articleId}")
    public Comment createComment(@PathVariable Long articleId, @RequestBody String content) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setArticle(article);
        comment.setUser(user);
        comment.setStatus("PENDING");

        Comment saved = commentRepository.save(comment);

        // Send notification to article author (if different from commenter)
        try {
            // Get the article author from the article's authorName field
            // Since we don't have a direct user reference in Article, we try to find by email
            String articleAuthorEmail = article.getAuthorName() + "@example.com";
            // For now, we'll use a simpler approach: find by email if stored
            User articleAuthor = userRepository.findByEmail(articleAuthorEmail).orElse(null);
            if (articleAuthor == null) {
                // Fallback: try to find the first admin user
                List<User> admins = userRepository.findAll().stream()
                        .filter(u -> "ADMIN".equals(u.getRole()))
                        .toList();
                if (!admins.isEmpty()) {
                    articleAuthor = admins.get(0);
                }
            }
            if (articleAuthor != null) {
                notificationService.sendCommentNotification(saved, user, articleAuthor);
            }
        } catch (Exception e) {
            System.out.println("⚠️ Could not send comment notification: " + e.getMessage());
        }

        auditLogService.log("CREATE", "Comment", saved.getId(), "Created comment by: " + user.getFullName());
        return saved;
    }

    // Reply to a comment (nested)
    @PostMapping("/reply/{parentId}")
    public Comment replyToComment(@PathVariable Long parentId, @RequestBody String content) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Comment parent = commentRepository.findById(parentId)
                .orElseThrow(() -> new RuntimeException("Parent comment not found"));

        Article article = parent.getArticle();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment reply = new Comment();
        reply.setContent(content);
        reply.setArticle(article);
        reply.setUser(user);
        reply.setParent(parent);
        reply.setStatus("PENDING");

        Comment saved = commentRepository.save(reply);

        // Send notification to parent comment author (if different from replier)
        try {
            User parentAuthor = parent.getUser();
            if (parentAuthor != null && !parentAuthor.getId().equals(user.getId())) {
                notificationService.sendReplyNotification(saved, user, parentAuthor);
            }
        } catch (Exception e) {
            System.out.println("⚠️ Could not send reply notification: " + e.getMessage());
        }

        auditLogService.log("CREATE", "Comment", saved.getId(), "Created reply by: " + user.getFullName() + " to comment " + parentId);
        return saved;
    }

    // Approve comment (Admin/Editor)
    @PutMapping("/{id}/approve")
    public Comment approveComment(@PathVariable Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setStatus("APPROVED");
        Comment saved = commentRepository.save(comment);
        auditLogService.log("APPROVE", "Comment", id, "Approved comment by: " + comment.getUser().getFullName());
        return saved;
    }

    // Reject comment
    @PutMapping("/{id}/reject")
    public Comment rejectComment(@PathVariable Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setStatus("REJECTED");
        Comment saved = commentRepository.save(comment);
        auditLogService.log("REJECT", "Comment", id, "Rejected comment by: " + comment.getUser().getFullName());
        return saved;
    }

    // Delete comment (Admin/Editor)
    @DeleteMapping("/{id}")
    public void deleteComment(@PathVariable Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        auditLogService.log("DELETE", "Comment", id, "Deleted comment by: " + comment.getUser().getFullName());
        commentRepository.deleteById(id);
    }
}