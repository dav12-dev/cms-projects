package com.cms.cms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("landing");
        registry.addViewController("/landing").setViewName("landing");
        registry.addViewController("/register").setViewName("register");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/forgot-password").setViewName("forgot-password");
        registry.addViewController("/reset-password").setViewName("reset-password");
        registry.addViewController("/dashboard").setViewName("dashboard");
        registry.addViewController("/users").setViewName("users");
        registry.addViewController("/categories").setViewName("categories");
        registry.addViewController("/articles").setViewName("articles");
        registry.addViewController("/tags").setViewName("tags");
        registry.addViewController("/pages").setViewName("pages");
        registry.addViewController("/media").setViewName("media");
        registry.addViewController("/profile").setViewName("profile");
        registry.addViewController("/settings").setViewName("settings");
        registry.addViewController("/comments").setViewName("comments");
        registry.addViewController("/menus").setViewName("menus");
        registry.addViewController("/products").setViewName("products");
        registry.addViewController("/widgets").setViewName("widgets");
        registry.addViewController("/forms").setViewName("forms");
        registry.addViewController("/faqs").setViewName("faqs");
        registry.addViewController("/testimonials").setViewName("testimonials");
        registry.addViewController("/events").setViewName("events");
        registry.addViewController("/calendar").setViewName("calendar");
        registry.addViewController("/portfolios").setViewName("portfolios");
        registry.addViewController("/jobs").setViewName("jobs");
        registry.addViewController("/news").setViewName("news");
        registry.addViewController("/backup").setViewName("backup");
        registry.addViewController("/version-history").setViewName("version-history");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}