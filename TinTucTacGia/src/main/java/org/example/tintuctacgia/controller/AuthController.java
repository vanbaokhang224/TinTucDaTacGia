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

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }

    // LOGIN
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

        // Bước 2: Kiểm tra name
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

        // Tất cả đúng → cấp token
        String token = jwtService.generateToken(user.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Đăng nhập thành công");
        response.put("token", token);
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());

        return ResponseEntity.ok(response);
    }

    // REFRESH TOKEN - lấy token mới khi sắp hết hạn
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
            @AuthenticationPrincipal User currentUser
    ) {
        String newToken = jwtService.generateToken(currentUser.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Token đã được làm mới");
        response.put("token", newToken);

        return ResponseEntity.ok(response);
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

    // GET USER BY ID - chỉ ADMIN hoặc chính user đó
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

    // UPDATE USER - chỉ ADMIN hoặc chính user đó
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