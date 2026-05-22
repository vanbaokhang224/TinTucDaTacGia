package org.example.tintuctacgia.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class TokenBlacklistService {

    // Lưu token đã logout vào memory
    // Nếu sau này dùng Redis thì thay Set này bằng Redis cache
    private final Set<String> blacklistedTokens = new HashSet<>();

    // Thêm token vào blacklist khi logout
    public void blacklist(String token) {
        blacklistedTokens.add(token);
    }

    // Kiểm tra token có bị blacklist không
    public boolean isBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}
