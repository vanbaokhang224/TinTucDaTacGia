package org.example.tintuctacgia.repository;

import org.example.tintuctacgia.entity.User;
import org.example.tintuctacgia.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    // Đếm theo role - dùng cho thống kê
    long countByRole(Role role);
}