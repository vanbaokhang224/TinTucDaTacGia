package org.example.tintuctacgia.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.example.tintuctacgia.dto.auth.LoginRequest;
import org.example.tintuctacgia.dto.auth.RegisterRequest;
import org.example.tintuctacgia.entity.User;
import org.example.tintuctacgia.enums.Role;
import org.example.tintuctacgia.exception.UnauthorizedException;
import org.example.tintuctacgia.service.AuthService;
import org.example.tintuctacgia.service.JwtService;
import org.example.tintuctacgia.service.TokenBlacklistService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final AuthService authService;
    private final TokenBlacklistService tokenBlacklistService;

    // REGISTER - mặc định READER, không cho chọn role
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

    // LOGIN - chỉ email + password
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request
    ) {
        try {
            User user = authService.login(request);
            String token = jwtService.generateToken(user.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Đăng nhập thành công");
            response.put("token", token);
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            response.put("role", user.getRole());

            return ResponseEntity.ok(response);
        } catch (UnauthorizedException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader("Authorization") String authHeader
    ) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            tokenBlacklistService.blacklist(token);
        }
        return ResponseEntity.ok(Map.of("message", "Đăng xuất thành công"));
    }

    // REFRESH TOKEN
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
            @AuthenticationPrincipal User currentUser
    ) {
        String newToken = jwtService.generateToken(currentUser.getEmail());
        return ResponseEntity.ok(Map.of(
                "message", "Token đã được làm mới",
                "token", newToken
        ));
    }

    // GET ALL USERS - chỉ ADMIN
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Chỉ ADMIN mới xem được danh sách user"));
        }
        return ResponseEntity.ok(authService.getAllUsers());
    }

    // GET USER BY ID - ADMIN hoặc chính user đó
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isSelf = currentUser.getId().equals(id);

        if (!isAdmin && !isSelf) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Bạn không có quyền xem thông tin user này"));
        }
        return ResponseEntity.ok(authService.getUserById(id));
    }

    // UPDATE USER INFO - ADMIN hoặc chính user đó
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody User updatedUser,
            @AuthenticationPrincipal User currentUser
    ) {
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isSelf = currentUser.getId().equals(id);

        if (!isAdmin && !isSelf) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Bạn không có quyền cập nhật user này"));
        }
        return ResponseEntity.ok(authService.updateUser(id, updatedUser));
    }

    // CHANGE ROLE - chỉ ADMIN mới được đổi role cho user
    // VD: PUT /api/auth/users/2/role
    // Body: { "role": "AUTHOR" }
    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> changeRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Chỉ ADMIN mới có quyền đổi role"));
        }

        try {
            Role newRole = Role.valueOf(body.get("role").toUpperCase());
            return ResponseEntity.ok(authService.changeRole(id, newRole));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Role không hợp lệ. Chỉ chấp nhận: READER, AUTHOR, EDITOR, ADMIN"));
        } catch (UnauthorizedException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // DELETE USER - chỉ ADMIN
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Chỉ ADMIN mới có quyền xóa user"));
        }
        authService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "Xóa user thành công"));
    }
}