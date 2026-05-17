package org.example.tintuctacgia.controller;

import lombok.RequiredArgsConstructor;
import org.example.tintuctacgia.dto.LoginRequest;
import org.example.tintuctacgia.entity.User;
import org.example.tintuctacgia.repository.UserRepository;
import org.example.tintuctacgia.service.AuthService;
import org.example.tintuctacgia.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        return ResponseEntity.ok(authService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.badRequest().body("Email not found");
        }

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            return ResponseEntity.badRequest().body("Wrong password");
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
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        authService.deleteUser(id);
        return ResponseEntity.ok("Xóa thành công");
    }
}
