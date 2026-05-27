package org.example.tintuctacgia.dto.reaction;

import lombok.*;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class BookmarkResponse {
    private Long id;
    private Long postId;
    private String postTitle;
    private String postSlug;
    private LocalDateTime createdAt;
}