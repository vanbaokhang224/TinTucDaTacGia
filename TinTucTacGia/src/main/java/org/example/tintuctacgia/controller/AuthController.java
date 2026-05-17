package org.example.tintuctacgia.controller;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.example.tintuctacgia.dto.LoginRequest;
import org.example.tintuctacgia.dto.RegisterRequest;
import org.example.tintuctacgia.entity.User;
import org.example.tintuctacgia.enums.Role;
import org.example.tintuctacgia.repository.UserRepository;
import org.example.tintuctacgia.service.AuthService;
import org.example.tintuctacgia.service.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(
                authService.register(request)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request
    ) {
        // Bước 1: Tìm user theo email
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Email không tồn tại");
        }

        // Bước 2: Kiểm tra name có khớp không
        if (!user.getName().equalsIgnoreCase(request.getName())) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Tên không khớp với tài khoản này");
        }

        // Bước 3: Kiểm tra password
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())
        ) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Sai mật khẩu");
        }

        // Tất cả đều khớp → cấp token kèm thông tin
        String token = jwtService.generateToken(user.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Đăng nhập thành công");
        response.put("token", token);
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());

        return ResponseEntity.ok(response);
    }

    // Chỉ ADMIN hoặc chính user đó mới được update
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
                    .body("Bạn không có quyền cập nhật user này");
        }

        return ResponseEntity.ok(
                authService.updateUser(id, updatedUser)
        );
    }

    // Chỉ ADMIN mới được xóa user
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body("Chỉ ADMIN mới có quyền xóa user");
        }

        authService.deleteUser(id);
        return ResponseEntity.ok("Xóa thành công");
    }
}