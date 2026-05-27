package org.example.tintuctacgia.dto.post;

import lombok.*;
import org.example.tintuctacgia.dto.tag.TagResponse;
import org.example.tintuctacgia.enums.PostStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {

    private Long id;
    private String title;
    private String slug;
    private String content;
    private PostStatus status;
    private String rejectedReason;

    // Category
    private Long categoryId;
    private String categoryName;

    // Tags
    private List<TagResponse> tags;

    // Tác giả
    private String authorName;
    private String authorEmail;

    // Người duyệt
    private String reviewedByName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
}