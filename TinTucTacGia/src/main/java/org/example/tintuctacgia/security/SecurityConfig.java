package org.example.tintuctacgia.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
                            Map<String, String> body = new HashMap<>();
                            body.put("message", "Bạn chưa đăng nhập. Vui lòng đăng nhập để tiếp tục!");
                            new ObjectMapper().writeValue(response.getOutputStream(), body);
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
                            Map<String, String> body = new HashMap<>();
                            body.put("message", "Bạn không có quyền để vào khu vực này!");
                            new ObjectMapper().writeValue(response.getOutputStream(), body);
                        })
                )
                .authorizeHttpRequests(auth -> auth

                        // ===== AUTH =====
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/auth/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/auth/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/auth/**").authenticated()

                        // ===== SWAGGER =====
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // ===== CATEGORY =====
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/categories/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/categories/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/categories/**").authenticated()

                        // ===== TAG =====
                        .requestMatchers(HttpMethod.GET, "/api/tags/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/tags/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/tags/**").authenticated()

                        // ===== POST =====
                        .requestMatchers(HttpMethod.GET, "/api/posts/my-posts").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/posts/pending-review").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/posts").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts/search").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts/slug/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts/by-category/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts/by-author/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/posts/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/posts/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/posts/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/**").authenticated()

                        // ===== COMMENT =====
                        .requestMatchers("/api/comments/**").authenticated()

                        // ===== REACTION =====
                        .requestMatchers("/api/reactions/**").authenticated()

                        // ===== BOOKMARK =====
                        .requestMatchers("/api/bookmarks/**").authenticated()

                        // ===== PROFILE =====
                        // /me cần đăng nhập
                        .requestMatchers(HttpMethod.GET, "/api/profiles/me").authenticated()
                        // GET profile công khai
                        .requestMatchers(HttpMethod.GET, "/api/profiles/**").permitAll()
                        // PUT cần đăng nhập
                        .requestMatchers(HttpMethod.PUT, "/api/profiles/**").authenticated()

                        // ===== STATISTICS =====
                        .requestMatchers("/api/statistics/**").authenticated()

                        // ===== FILES (Cloudinary) =====
                        .requestMatchers(HttpMethod.POST, "/api/files/**").hasAnyRole("AUTHOR", "ADMIN", "EDITOR")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}