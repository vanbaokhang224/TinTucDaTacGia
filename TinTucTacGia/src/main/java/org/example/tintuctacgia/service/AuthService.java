package org.example.tintuctacgia.service;

import lombok.RequiredArgsConstructor;

import org.example.tintuctacgia.dto.auth.LoginRequest;
import org.example.tintuctacgia.dto.auth.RegisterRequest;
import org.example.tintuctacgia.dto.auth.UserResponse;
import org.example.tintuctacgia.entity.*;
import org.example.tintuctacgia.enums.Role;
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

    // REGISTER - luôn tạo Reader
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(
                    "Email '" + request.getEmail() + "' đã được sử dụng"
            );
        }

        // FIX: Tạo Reader entity (bảng readers)
        Reader reader = new Reader();
        reader.setName(request.getName());
        reader.setEmail(request.getEmail());
        reader.setPassword(passwordEncoder.encode(request.getPassword()));
        reader.setDateOfBirth(request.getDateOfBirth());
        reader.setRole(Role.READER);

        return UserMapper.toResponse(userRepository.save(reader));
    }

    // LOGIN - chỉ email + password
    public User login(LoginRequest request) {
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Email không tồn tại"));

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

    // UPDATE USER INFO
    public UserResponse updateUser(Long id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (updatedUser.getName() != null) user.setName(updatedUser.getName());
        if (updatedUser.getEmail() != null) user.setEmail(updatedUser.getEmail());
        if (updatedUser.getDateOfBirth() != null) user.setDateOfBirth(updatedUser.getDateOfBirth());
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        return UserMapper.toResponse(userRepository.save(user));
    }

    // CHANGE ROLE - chỉ ADMIN mới được đổi
    // FIX: Tạo entity mới đúng type khi đổi role
    public UserResponse changeRole(Long id, Role newRole) {
        User oldUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (oldUser.getRole() == Role.ADMIN) {
            throw new UnauthorizedException("Không thể thay đổi role của ADMIN");
        }

        // Tạo entity mới theo role mới
        User newUser = createUserByRole(newRole);
        newUser.setId(oldUser.getId());
        newUser.setName(oldUser.getName());
        newUser.setEmail(oldUser.getEmail());
        newUser.setPassword(oldUser.getPassword());
        newUser.setDateOfBirth(oldUser.getDateOfBirth());
        newUser.setRole(newRole);

        // Xóa user cũ và lưu user mới
        userRepository.delete(oldUser);
        return UserMapper.toResponse(userRepository.save(newUser));
    }

    // Helper - tạo entity đúng type theo role
    private User createUserByRole(Role role) {
        return switch (role) {
            case READER -> new Reader();
            case AUTHOR -> new Author();
            case EDITOR -> new Editor();
            case ADMIN -> new Admin();
        };
    }

    // DELETE USER
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}