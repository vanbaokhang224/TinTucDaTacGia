package org.example.tintuctacgia.mapper;

import org.example.tintuctacgia.dto.PostResponse;
import org.example.tintuctacgia.entity.Post;

public class PostMapper {

    // Convert Post entity → PostResponse DTO
    public static PostResponse toResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .category(post.getCategory())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .authorName(post.getUser().getName())
                .authorEmail(post.getUser().getEmail())
                .build();
    }
}
