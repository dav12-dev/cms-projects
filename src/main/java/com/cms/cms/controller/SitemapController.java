package com.cms.cms.controller;

import com.cms.cms.entity.Article;
import com.cms.cms.entity.Page;
import com.cms.cms.repository.ArticleRepository;
import com.cms.cms.repository.PageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class SitemapController {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private PageRepository pageRepository;

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public String generateSitemap() {
        List<Article> publishedArticles = articleRepository.findByStatus("PUBLISHED");
        List<Page> publishedPages = pageRepository.findAll();

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

        // Homepage
        xml.append("  <url>\n");
        xml.append("    <loc>https://cms-final-klqt.onrender.com/</loc>\n");
        xml.append("    <priority>1.0</priority>\n");
        xml.append("    <changefreq>daily</changefreq>\n");
        xml.append("  </url>\n");

        // Published articles
        for (Article article : publishedArticles) {
            xml.append("  <url>\n");
            xml.append("    <loc>https://cms-final-klqt.onrender.com/article/" + article.getSlug() + "</loc>\n");
            xml.append("    <priority>0.8</priority>\n");
            xml.append("    <changefreq>weekly</changefreq>\n");
            if (article.getUpdatedAt() != null) {
                xml.append("    <lastmod>" + article.getUpdatedAt().format(DateTimeFormatter.ISO_DATE) + "</lastmod>\n");
            }
            xml.append("  </url>\n");
        }

        // Published pages
        for (Page page : publishedPages) {
            xml.append("  <url>\n");
            xml.append("    <loc>https://cms-final-klqt.onrender.com/page/" + page.getSlug() + "</loc>\n");
            xml.append("    <priority>0.6</priority>\n");
            xml.append("    <changefreq>monthly</changefreq>\n");
            if (page.getUpdatedAt() != null) {
                xml.append("    <lastmod>" + page.getUpdatedAt().format(DateTimeFormatter.ISO_DATE) + "</lastmod>\n");
            }
            xml.append("  </url>\n");
        }

        xml.append("</urlset>");
        return xml.toString();
    }
}