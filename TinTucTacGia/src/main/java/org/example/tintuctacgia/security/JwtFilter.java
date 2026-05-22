package org.example.tintuctacgia.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.example.tintuctacgia.entity.User;
import org.example.tintuctacgia.repository.UserRepository;
import org.example.tintuctacgia.service.JwtService;
import org.example.tintuctacgia.service.TokenBlacklistService;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();
        String method = request.getMethod();

        System.out.println("🔍 Request: " + method + " " + path);

        // Chỉ skip đúng 2 route không cần token
        boolean isLoginOrRegister =
                (path.equals("/api/auth/login") && method.equals("POST")) ||
                        (path.equals("/api/auth/register") && method.equals("POST"));

        if (isLoginOrRegister) {
            System.out.println("⏭️ Skipping filter for: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        System.out.println("📋 Authorization header: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("❌ No token found, passing to security config");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);

            // Kiểm tra token có bị blacklist không
            if (tokenBlacklistService.isBlacklisted(token)) {
                System.out.println("🚫 Token is blacklisted!");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(
                        "{\"message\": \"Token đã hết hiệu lực. Vui lòng đăng nhập lại!\"}"
                );
                return;
            }

            String email = jwtService.extractEmail(token);
            System.out.println("📧 Email from token: " + email);

            User user = userRepository
                    .findByEmail(email)
                    .orElse(null);

            if (user != null) {
                System.out.println("✅ User found: " + user.getEmail() + " | Role: " + user.getRole());

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                List.of(new SimpleGrantedAuthority(
                                        "ROLE_" + user.getRole().name()
                                ))
                        );
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authToken);

                System.out.println("✅ Authentication set successfully for: " + user.getEmail());
            } else {
                System.out.println("❌ User not found for email: " + email);
            }

        } catch (Exception e) {
            System.out.println("💥 Token error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    "{\"message\": \"Token không hợp lệ hoặc đã hết hạn!\"}"
            );
            return;
        }

        filterChain.doFilter(request, response);
    }
}