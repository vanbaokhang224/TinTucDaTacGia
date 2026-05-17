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
    public SecurityFilterChain filterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // Custom message khi bị chặn
                .exceptionHandling(ex -> ex

                        // Chưa đăng nhập → 401
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
                            Map<String, String> body = new HashMap<>();
                            body.put("message", "Bạn chưa đăng nhập. Vui lòng đăng nhập để tiếp tục!");
                            new ObjectMapper().writeValue(response.getOutputStream(), body);
                        })

                        // Đã đăng nhập nhưng không đủ quyền → 403
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
                            Map<String, String> body = new HashMap<>();
                            body.put("message", "Bạn không có quyền để vào khu vực này!");
                            new ObjectMapper().writeValue(response.getOutputStream(), body);
                        })
                )

                .authorizeHttpRequests(auth -> auth

                        // FIX: Chỉ permitAll đúng 2 route login và register
                        // Không dùng /api/auth/** vì sẽ bỏ qua cả update/delete
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/register",
                                "/api/auth/login"
                        ).permitAll()

                        // SWAGGER
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Update/Delete user → phải đăng nhập
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/auth/update/**"
                        ).authenticated()

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/auth/delete/**"
                        ).authenticated()

                        // XEM POST - ai cũng được
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/posts/**"
                        ).permitAll()

                        // TẠO POST - chỉ ADMIN, AUTHOR
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/posts/**"
                        ).hasAnyRole("ADMIN", "AUTHOR")

                        // SỬA POST - chỉ ADMIN, AUTHOR
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/posts/**"
                        ).hasAnyRole("ADMIN", "AUTHOR")

                        // XÓA POST - chỉ ADMIN
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/posts/**"
                        ).hasRole("ADMIN")

                        // COMMENT - phải đăng nhập
                        .requestMatchers(
                                "/api/comments/**"
                        ).authenticated()

                        .anyRequest().authenticated()
                )

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