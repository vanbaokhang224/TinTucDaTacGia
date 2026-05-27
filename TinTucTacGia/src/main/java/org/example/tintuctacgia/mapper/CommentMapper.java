package org.example.tintuctacgia.mapper;

import org.example.tintuctacgia.dto.comment.CommentResponse;
import org.example.tintuctacgia.entity.Comment;

public class CommentMapper {

    // Convert Comment entity → CommentResponse DTO
    public static CommentResponse toResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .userName(comment.getUser().getName())
                .postId(comment.getPost().getId())
                .build();
    }
}

