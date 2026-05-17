package org.example.tintuctacgia.controller;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.example.tintuctacgia.dto.LoginRequest;
import org.example.tintuctacgia.dto.RegisterRequest;

import org.example.tintuctacgia.entity.User;

import org.example.tintuctacgia.repository.UserRepository;

import org.example.tintuctacgia.service.AuthService;
import org.example.tintuctacgia.service.JwtService;

import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.web.bind.annotation.*;

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

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {

            return ResponseEntity
                    .badRequest()
                    .body("Email not found");
        }

        if (
                !passwordEncoder.matches(
                        request.getPassword(),
                        user.getPassword()
                )
        ) {

            return ResponseEntity
                    .badRequest()
                    .body("Wrong password");
        }

        String token =
                jwtService.generateToken(
                        user.getEmail()
                );

        return ResponseEntity.ok(token);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody User user
    ) {

        return ResponseEntity.ok(
                authService.updateUser(id, user)
        );
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(
            @PathVariable Long id
    ) {

        authService.deleteUser(id);

        return ResponseEntity.ok(
                "Xóa thành công"
        );
    }
}
