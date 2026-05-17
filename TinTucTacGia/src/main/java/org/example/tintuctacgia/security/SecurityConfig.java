package org.example.tintuctacgia.security;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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

                // TẮT CSRF
                .csrf(csrf -> csrf.disable())

                // JWT STATELESS
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // PHÂN QUYỀN
                .authorizeHttpRequests(auth -> auth

                        // AUTH API
                        .requestMatchers(
                                "/api/auth/**"
                        ).permitAll()

                        // SWAGGER
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // XEM POST
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/posts/**"
                        ).permitAll()

                        // TẠO POST
                        // FIX: Đổi hasAnyAuthority("ADMIN","AUTHOR") → hasAnyRole("ADMIN","AUTHOR")
                        // vì User.getAuthorities() trả về "ROLE_ADMIN", "ROLE_AUTHOR"
                        // hasAnyRole tự thêm prefix "ROLE_" khi so sánh
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/posts/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "AUTHOR"
                        )

                        // UPDATE POST
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/posts/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "AUTHOR"
                        )

                        // DELETE POST
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/posts/**"
                        ).hasRole(
                                "ADMIN"
                        )

                        // COMMENT
                        .requestMatchers(
                                "/api/comments/**"
                        ).authenticated()

                        // API KHÁC
                        .anyRequest()
                        .authenticated()
                )

                // JWT FILTER
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}