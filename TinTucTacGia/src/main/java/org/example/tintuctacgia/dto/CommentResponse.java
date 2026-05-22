package org.example.tintuctacgia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private Long id;
    private String content;
    private LocalDateTime createdAt;

    // Chỉ trả tên người comment, không trả toàn bộ User object
    private String userName;

    // Chỉ trả id bài viết
    private Long postId;
}

