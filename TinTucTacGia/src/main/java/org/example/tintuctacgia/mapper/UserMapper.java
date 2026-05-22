package org.example.tintuctacgia.mapper;

import org.example.tintuctacgia.dto.UserResponse;
import org.example.tintuctacgia.entity.User;

public class UserMapper {

    // Convert User entity → UserResponse DTO
    public static UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .dateOfBirth(user.getDateOfBirth())
                .role(user.getRole())
                .build();
    }
}
