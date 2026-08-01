package com.cms.cms.config;

import com.cms.cms.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .userDetailsService(userDetailsService)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/landing",
                                "/register",
                                "/api/users/register",
                                "/login",
                                "/forgot-password",
                                "/reset-password",
                                "/api/users/forgot-password",
                                "/api/users/reset-password",
                                "/api/users/theme",
                                "/css/**",
                                "/js/**",
                                "/webjars/**",
                                "/images/**",
                                "/assets/**",
                                "/sitemap.xml",
                                "/robots.txt"
                        ).permitAll()
                        .requestMatchers("/dashboard", "/profile", "/api/users/profile/**").hasAnyRole("USER", "EDITOR", "ADMIN")
                        .requestMatchers(
                                "/articles",
                                "/api/articles/**",
                                "/categories",
                                "/api/categories/**",
                                "/tags",
                                "/api/tags/**",
                                "/comments",
                                "/api/comments/**",
                                "/media",
                                "/api/media/**",
                                "/pages",
                                "/api/pages/**",
                                "/menus",
                                "/api/menus/**",
                                "/products",
                                "/api/products/**",
                                "/widgets",
                                "/api/widgets/**",
                                "/forms",
                                "/api/forms/**",
                                "/faqs",
                                "/api/faqs/**",
                                "/testimonials",
                                "/api/testimonials/**",
                                "/events",
                                "/api/events/**",
                                "/calendar",
                                "/portfolios",
                                "/api/portfolios/**",
                                "/jobs",
                                "/api/jobs/**",
                                "/news",
                                "/api/news/**"
                        ).hasAnyRole("EDITOR", "ADMIN")
                        .requestMatchers(
                                "/users",
                                "/api/users/**",
                                "/settings",
                                "/api/settings/**",
                                "/audit-logs",
                                "/api/audit-logs/**",
                                "/backup",
                                "/api/backup/**",
                                "/version-history",
                                "/api/version-history/**"
                        ).hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers(
                                "/api/users/register",
                                "/api/login",
                                "/api/users/forgot-password",
                                "/api/users/reset-password",
                                "/api/users/theme"
                        )
                );

        return http.build();
    }
}