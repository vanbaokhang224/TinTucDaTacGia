package org.example.tintuctacgia.mapper;

import org.example.tintuctacgia.dto.post.PostResponse;
import org.example.tintuctacgia.entity.Post;

import java.util.stream.Collectors;

public class PostMapper {

    public static PostResponse toResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .content(post.getContent())
                .thumbnail(post.getThumbnail())
                .status(post.getStatus())
                .rejectedReason(post.getRejectedReason())
                .categoryId(post.getCategory() != null ? post.getCategory().getId() : null)
                .categoryName(post.getCategory() != null ? post.getCategory().getName() : null)
                .tags(post.getTags() != null ? post.getTags().stream()
                                               .map(TagMapper::toResponse)
                                               .collect(Collectors.toList()) : null)
                .authorName(post.getUser() != null ? post.getUser().getName() : null)
                .authorEmail(post.getUser() != null ? post.getUser().getEmail() : null)
                .reviewedByName(post.getReviewedBy() != null ? post.getReviewedBy().getName() : null)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .publishedAt(post.getPublishedAt())
                .build();
    }
}
