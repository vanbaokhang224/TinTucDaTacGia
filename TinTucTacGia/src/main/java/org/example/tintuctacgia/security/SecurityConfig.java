package org.example.tintuctacgia.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.example.tintuctacgia.security.JwtFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // Tắt CSRF
                .csrf(csrf -> csrf.disable())

                // Phân quyền
                .authorizeHttpRequests(auth -> auth

                        // PUBLIC API
                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // DELETE USER
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/auth/delete/**"
                        ).hasRole("ADMIN")

                        // GET POSTS
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/posts/**"
                        ).permitAll()

                        // CREATE POST
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/posts/**"
                        ).hasAnyRole("ADMIN", "AUTHOR")

                        // UPDATE POST
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/posts/**"
                        ).hasAnyRole("ADMIN", "AUTHOR")

                        // DELETE POST
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/posts/**"
                        ).hasRole("ADMIN")

                        // CREATE COMMENT
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/comments/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "AUTHOR",
                                "USER"
                        )

                        // DELETE COMMENT
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/comments/**"
                        ).hasRole("ADMIN")

                        // Các API khác
                        .anyRequest()
                        .authenticated()
                );
        http.addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    // Password Encoder
    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}