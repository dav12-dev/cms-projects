package com.cms.cms.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String status; // DRAFT, PUBLISHED, SCHEDULED

    private String authorName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "publish_at")
    private LocalDateTime publishAt;

    @Column(name = "meta_title")
    private String metaTitle;

    @Column(name = "meta_description", length = 500)
    private String metaDescription;

    @Column(name = "canonical_url")
    private String canonicalUrl;

    private int likes = 0;

    @Column(name = "view_count")
    private int viewCount = 0;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "article_tags",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();

    // ===== Constructors =====
    public Article() {}

    // ===== Getters and Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getPublishAt() { return publishAt; }
    public void setPublishAt(LocalDateTime publishAt) { this.publishAt = publishAt; }

    public String getMetaTitle() { return metaTitle; }
    public void setMetaTitle(String metaTitle) { this.metaTitle = metaTitle; }

    public String getMetaDescription() { return metaDescription; }
    public void setMetaDescription(String metaDescription) { this.metaDescription = metaDescription; }

    public String getCanonicalUrl() { return canonicalUrl; }
    public void setCanonicalUrl(String canonicalUrl) { this.canonicalUrl = canonicalUrl; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public List<Comment> getComments() { return comments; }
    public void setComments(List<Comment> comments) { this.comments = comments; }

    public List<Tag> getTags() { return tags; }
    public void setTags(List<Tag> tags) { this.tags = tags; }

    // ===== Lifecycle =====
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = "DRAFT";
        if (slug == null || slug.isEmpty()) slug = generateSlug(title);
        if (authorName == null || authorName.isEmpty()) authorName = "Admin";
        if (metaTitle == null || metaTitle.isEmpty()) metaTitle = title;
        if (metaDescription == null || metaDescription.isEmpty()) {
            String plainText = content != null ? content.replaceAll("<[^>]*>", "") : "";
            metaDescription = plainText.length() > 160 ? plainText.substring(0, 157) + "..." : plainText;
            if (metaDescription.isEmpty()) metaDescription = title;
        }
        viewCount = 0;
        // Auto-generate canonical URL if not set
        if (canonicalUrl == null || canonicalUrl.isEmpty()) {
            canonicalUrl = "https://cms-final-klqt.onrender.com/article/" + slug;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (slug == null || slug.isEmpty()) slug = generateSlug(title);
        if (metaTitle == null || metaTitle.isEmpty()) metaTitle = title;
        if (authorName == null || authorName.isEmpty()) authorName = "Admin";
        if (canonicalUrl == null || canonicalUrl.isEmpty()) {
            canonicalUrl = "https://cms-final-klqt.onrender.com/article/" + slug;
        }
    }

    // ===== Helper Methods =====
    public String generateSlug(String title) {
        if (title == null) return "";
        return title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }

    public String getReadingTime() {
        if (content == null) return "1 min read";
        String plainText = content.replaceAll("<[^>]*>", "");
        if (plainText.trim().isEmpty()) return "1 min read";
        int wordCount = plainText.split("\\s+").length;
        int minutes = Math.max(1, (int) Math.ceil(wordCount / 200.0));
        return minutes + " min read";
    }

    public void incrementViewCount() {
        this.viewCount++;
    }

    // ===== Version History Method =====
    public ArticleVersion createVersion(String createdBy, String changeComment) {
        ArticleVersion version = new ArticleVersion();
        version.setArticleId(this.id);
        version.setTitle(this.title);
        version.setContent(this.content);
        version.setStatus(this.status);
        version.setAuthorName(this.authorName);
        version.setMetaTitle(this.metaTitle);
        version.setMetaDescription(this.metaDescription);
        version.setCreatedBy(createdBy);
        version.setChangeComment(changeComment);
        return version;
    }
}