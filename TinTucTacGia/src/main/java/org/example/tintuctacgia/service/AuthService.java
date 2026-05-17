package org.example.tintuctacgia.service;

import lombok.RequiredArgsConstructor;

import org.example.tintuctacgia.dto.RegisterRequest;
import org.example.tintuctacgia.entity.User;
import org.example.tintuctacgia.exception.DuplicateEmailException;
import org.example.tintuctacgia.exception.UserNotFoundException;
import org.example.tintuctacgia.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(RegisterRequest request) {

        // Kiểm tra email trùng trước khi tạo user
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(
                    "Email '" + request.getEmail() + "' đã được sử dụng"
            );
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDateOfBirth(request.getDateOfBirth());
        user.setRole(request.getRole());

        return userRepository.save(user);
    }

    public User updateUser(Long id, User updatedUser) {

        // Dùng UserNotFoundException thay vì RuntimeException
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());

        // Chỉ update password nếu có nhập mới
        if (updatedUser.getPassword() != null
                && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(
                    passwordEncoder.encode(updatedUser.getPassword())
            );
        }

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {

        // Kiểm tra tồn tại trước khi xóa
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }

        userRepository.deleteById(id);
    }
}