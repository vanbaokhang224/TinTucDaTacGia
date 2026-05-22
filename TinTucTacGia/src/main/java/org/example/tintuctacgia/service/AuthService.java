package org.example.tintuctacgia.service;

import lombok.RequiredArgsConstructor;

import org.example.tintuctacgia.dto.LoginRequest;
import org.example.tintuctacgia.dto.RegisterRequest;
import org.example.tintuctacgia.dto.UserResponse;
import org.example.tintuctacgia.entity.User;
import org.example.tintuctacgia.exception.DuplicateEmailException;
import org.example.tintuctacgia.exception.UnauthorizedException;
import org.example.tintuctacgia.exception.UserNotFoundException;
import org.example.tintuctacgia.mapper.UserMapper;
import org.example.tintuctacgia.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // REGISTER
    public UserResponse register(RegisterRequest request) {
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
        return UserMapper.toResponse(userRepository.save(user));
    }

    // FIX: Chuyển login logic vào Service thay vì để trong Controller
    public User login(LoginRequest request) {
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Email không tồn tại"));

        if (!user.getName().equalsIgnoreCase(request.getName())) {
            throw new UnauthorizedException("Tên không khớp với tài khoản này");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Sai mật khẩu");
        }

        return user;
    }

    // GET ALL USERS
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toResponse)
                .collect(Collectors.toList());
    }

    // GET USER BY ID
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return UserMapper.toResponse(user);
    }

    // UPDATE USER
    public UserResponse updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());

        if (updatedUser.getDateOfBirth() != null) {
            user.setDateOfBirth(updatedUser.getDateOfBirth());
        }

        if (updatedUser.getPassword() != null
                && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        return UserMapper.toResponse(userRepository.save(user));
    }

    // DELETE USER
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}