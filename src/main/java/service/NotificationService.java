package com.cms.cms.service;

import com.cms.cms.entity.Comment;
import com.cms.cms.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendCommentNotification(Comment comment, User commenter, User articleAuthor) {
        if (articleAuthor == null || commenter == null) return;
        if (commenter.getId().equals(articleAuthor.getId())) return; // Don't notify if same user

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(articleAuthor.getEmail());
            message.setSubject("New Comment on Your Article: " + comment.getArticle().getTitle());
            message.setText(
                    "Hello " + articleAuthor.getFullName() + ",\n\n" +
                            commenter.getFullName() + " has posted a comment on your article:\n\n" +
                            "Article: " + comment.getArticle().getTitle() + "\n" +
                            "Comment: " + comment.getContent() + "\n\n" +
                            "View the comment: https://cms-final-klqt.onrender.com/article/" + comment.getArticle().getSlug() + "\n\n" +
                            "Best regards,\nThe CMS Team"
            );
            mailSender.send(message);
            System.out.println("✅ Comment notification sent to: " + articleAuthor.getEmail());
        } catch (Exception e) {
            System.out.println("❌ Comment notification email not sent: " + e.getMessage());
        }
    }

    public void sendReplyNotification(Comment reply, User replier, User parentCommentAuthor) {
        if (parentCommentAuthor == null || replier == null) return;
        if (replier.getId().equals(parentCommentAuthor.getId())) return; // Don't notify if same user

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(parentCommentAuthor.getEmail());
            message.setSubject("New Reply to Your Comment");
            message.setText(
                    "Hello " + parentCommentAuthor.getFullName() + ",\n\n" +
                            replier.getFullName() + " has replied to your comment:\n\n" +
                            "Your comment: " + reply.getParent().getContent() + "\n" +
                            "Reply: " + reply.getContent() + "\n\n" +
                            "View the reply: https://cms-final-klqt.onrender.com/article/" + reply.getArticle().getSlug() + "\n\n" +
                            "Best regards,\nThe CMS Team"
            );
            mailSender.send(message);
            System.out.println("✅ Reply notification sent to: " + parentCommentAuthor.getEmail());
        } catch (Exception e) {
            System.out.println("❌ Reply notification email not sent: " + e.getMessage());
        }
    }
}