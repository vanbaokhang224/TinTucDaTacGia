package org.example.tintuctacgia.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    // Đọc secret key từ application.properties thay vì hardcode
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    //  Thời gian hết hạn token (1 giờ), đọc từ config
    @Value("${jwt.expiration:3600000}")
    private long EXPIRATION_TIME;

    // Lấy signing key an toàn
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // Tạo token
    public String generateToken(String email) {

        return Jwts.builder()

                .setSubject(email)

                .setIssuedAt(new Date())

                .setExpiration(
                        new Date(System.currentTimeMillis() + EXPIRATION_TIME)
                )

                // ✅ Dùng API mới, không deprecated
                .signWith(getSigningKey())

                .compact();
    }

    // Lấy email từ token
    public String extractEmail(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    // Extract claim
    public <T> T extractClaim(
            String token,
            Function<Claims, T> resolver
    ) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    // Extract all claims
    private Claims extractAllClaims(String token) {

        //  Dùng parserBuilder() thay vì parser() (không deprecated)
        return Jwts.parserBuilder()

                .setSigningKey(getSigningKey())

                .build()

                .parseClaimsJws(token)

                .getBody();
    }

    //  Thêm kiểm tra token hết hạn
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration)
                .before(new Date());
    }

    // Check token hợp lệ (email đúng VÀ chưa hết hạn)
    public boolean isTokenValid(String token, String email) {

        String extractedEmail = extractEmail(token);

        //  Check cả email lẫn expiration
        return extractedEmail.equals(email) && !isTokenExpired(token);
    }
}
