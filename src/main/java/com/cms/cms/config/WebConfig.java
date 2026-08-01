package com.cms.cms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Landing / Home page
        registry.addViewController("/").setViewName("landing");
        registry.addViewController("/landing").setViewName("landing");

        // Authentication pages
        registry.addViewController("/register").setViewName("register");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/forgot-password").setViewName("forgot-password");
        registry.addViewController("/reset-password").setViewName("reset-password");

        // Dashboard
        registry.addViewController("/dashboard").setViewName("dashboard");

        // Core management pages
        registry.addViewController("/users").setViewName("users");
        registry.addViewController("/categories").setViewName("categories");
        registry.addViewController("/articles").setViewName("articles");
        registry.addViewController("/tags").setViewName("tags");

        // Pages management
        registry.addViewController("/pages").setViewName("pages");

        // Media Library
        registry.addViewController("/media").setViewName("media");

        // User Profile
        registry.addViewController("/profile").setViewName("profile");

        // Settings
        registry.addViewController("/settings").setViewName("settings");

        // Comments management
        registry.addViewController("/comments").setViewName("comments");

        // Menus
        registry.addViewController("/menus").setViewName("menus");

        // Products
        registry.addViewController("/products").setViewName("products");

        // Widgets
        registry.addViewController("/widgets").setViewName("widgets");

        // Forms Builder
        registry.addViewController("/forms").setViewName("forms");

        // FAQs
        registry.addViewController("/faqs").setViewName("faqs");

        // Testimonials
        registry.addViewController("/testimonials").setViewName("testimonials");

        // Events
        registry.addViewController("/events").setViewName("events");

        // Calendar View
        registry.addViewController("/calendar").setViewName("calendar");

        // Backup & Restore
        registry.addViewController("/backup").setViewName("backup");

        // Version History
        registry.addViewController("/version-history").setViewName("version-history");

        // Portfolios
        registry.addViewController("/portfolios").setViewName("portfolios");

        // Jobs
        registry.addViewController("/jobs").setViewName("jobs");

        // News
        registry.addViewController("/news").setViewName("news");
    }
}