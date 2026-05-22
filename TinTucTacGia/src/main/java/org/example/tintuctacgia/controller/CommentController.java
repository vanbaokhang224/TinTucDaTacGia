package org.example.tintuctacgia.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.example.tintuctacgia.dto.CommentRequest;
import org.example.tintuctacgia.entity.User;
import org.example.tintuctacgia.service.CommentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // CREATE COMMENT
    @PostMapping("/{postId}")
    public ResponseEntity<?> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(
                commentService.createComment(postId, request, currentUser)
        );
    }

    // GET COMMENTS THEO BÀI VIẾT
    @GetMapping("/post/{postId}")
    public ResponseEntity<?> getCommentsByPost(
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(
                commentService.getCommentsByPost(postId)
        );
    }

    // GET ALL (dành cho admin)
    @GetMapping
    public ResponseEntity<?> getComments() {
        return ResponseEntity.ok(
                commentService.getComments()
        );
    }

    // UPDATE COMMENT - chỉ chủ comment mới được sửa
    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        try {
            return ResponseEntity.ok(
                    commentService.updateComment(id, request, currentUser)
            );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // DELETE COMMENT
    // ADMIN xóa tất cả, USER xóa của mình, AUTHOR không được xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        try {
            commentService.deleteComment(id, currentUser);
            return ResponseEntity.ok(
                    Map.of("message", "Xóa comment thành công")
            );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}