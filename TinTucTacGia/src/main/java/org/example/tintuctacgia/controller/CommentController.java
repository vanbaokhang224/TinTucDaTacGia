package org.example.tintuctacgia.controller;

import lombok.RequiredArgsConstructor;

import org.example.tintuctacgia.entity.Comment;
import org.example.tintuctacgia.entity.Post;
import org.example.tintuctacgia.entity.User;
import org.example.tintuctacgia.repository.PostRepository;
import org.example.tintuctacgia.repository.UserRepository;
import org.example.tintuctacgia.service.CommentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    // CREATE COMMENT
    @PostMapping("/{postId}")
    public ResponseEntity<?> createComment(
            @PathVariable Long postId,
            @RequestBody Comment comment,
            Principal principal
    ) {
        User user = userRepository
                .findByEmail(principal.getName())
                .orElseThrow();

        Post post = postRepository
                .findById(postId)
                .orElseThrow();

        comment.setUser(user);
        comment.setPost(post);

        return ResponseEntity.ok(
                commentService.createComment(comment)
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

    // UPDATE COMMENT
    // Chỉ chủ comment mới được sửa
    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(
            @PathVariable Long id,
            @RequestBody Comment comment,
            @AuthenticationPrincipal User currentUser
    ) {
        try {
            return ResponseEntity.ok(
                    commentService.updateComment(id, comment, currentUser)
            );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // DELETE COMMENT
    // Chỉ ADMIN hoặc chủ comment mới xóa được
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