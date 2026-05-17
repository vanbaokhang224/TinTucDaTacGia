package org.example.tintuctacgia.service;

import lombok.RequiredArgsConstructor;

import org.example.tintuctacgia.dto.RegisterRequest;

import org.example.tintuctacgia.entity.User;

import org.example.tintuctacgia.enums.Role;

import org.example.tintuctacgia.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public User register(RegisterRequest request) {

        User user = new User();

        user.setName(
                request.getName()
        );

        user.setEmail(
                request.getEmail()
        );

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        user.setDateOfBirth(
                request.getDateOfBirth()
        );

        user.setRole(
                request.getRole()
        );

        return userRepository.save(user);
    }
    public User updateUser(
            Long id,
            User updatedUser
    ) {

        User user = userRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "User not found"
                        )
                );

        user.setName(
                updatedUser.getName()
        );

        user.setEmail(
                updatedUser.getEmail()
        );

        // Nếu có nhập password mới
        if (
                updatedUser.getPassword() != null
                        &&
                        !updatedUser.getPassword().isEmpty()
        ) {

            user.setPassword(
                    passwordEncoder.encode(
                            updatedUser.getPassword()
                    )
            );
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {

        userRepository.deleteById(id);
    }
}