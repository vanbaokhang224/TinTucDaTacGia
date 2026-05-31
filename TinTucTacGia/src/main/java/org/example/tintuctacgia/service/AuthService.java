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
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    // REGISTER - luôn tạo Reader
    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(
                    "Email '" + request.getEmail() + "' đã được sử dụng"
            );
        }

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

    //CHANGE ROLE - giữ nguyên ID, không xóa tạo lại
    @Transactional
    public UserResponse changeRole(Long id, Role newRole) {
        User oldUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (oldUser.getRole() == Role.ADMIN) {
            throw new UnauthorizedException("Không thể thay đổi role của ADMIN");
        }

        if (oldUser.getRole() == newRole) {
            throw new UnauthorizedException("User đã có role " + newRole.name() + " rồi");
        }

        // Bước 1: Xóa record trong bảng con cũ
        String oldTable = getTableName(oldUser.getRole());
        entityManager.createNativeQuery(
                "DELETE FROM " + oldTable + " WHERE id = :id"
        ).setParameter("id", id).executeUpdate();

        // Bước 2: Cập nhật dtype và role trong bảng users (ID giữ nguyên)
        entityManager.createNativeQuery(
                        "UPDATE users SET dtype = :dtype, role = :role WHERE id = :id"
                )
                .setParameter("dtype", newRole.name())
                .setParameter("role", newRole.name())
                .setParameter("id", id)
                .executeUpdate();

        // Bước 3: Tạo record trong bảng con mới
        String newTable = getTableName(newRole);
        entityManager.createNativeQuery(
                "INSERT INTO " + newTable + " (id) VALUES (:id)"
        ).setParameter("id", id).executeUpdate();

        // Bước 4: Clear cache để reload entity mới
        entityManager.flush();
        entityManager.clear();

        // Bước 5: Reload và trả về user với role mới
        User updatedUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return UserMapper.toResponse(updatedUser);
    }

    // Helper - lấy tên bảng theo role
    private String getTableName(Role role) {
        return switch (role) {
            case READER -> "readers";
            case AUTHOR -> "authors";
            case EDITOR -> "editors";
            case ADMIN -> "admins";
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