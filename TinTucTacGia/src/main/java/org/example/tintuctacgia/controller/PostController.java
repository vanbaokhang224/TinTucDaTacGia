package org.example.tintuctacgia.controller;

import lombok.RequiredArgsConstructor;

import org.example.tintuctacgia.entity.Post;
import org.example.tintuctacgia.entity.User;
import org.example.tintuctacgia.enums.Role;
import org.example.tintuctacgia.service.PostService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // CREATE
    // Chỉ ADMIN hoặc AUTHOR mới được tạo bài
    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestBody Post post,
            @AuthenticationPrincipal User currentUser
    ) {
        // Nếu là USER thường → chặn
        if (currentUser.getRole() == Role.USER) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "message", "Bạn không có quyền truy cập vào chỗ này. Chỉ AUTHOR hoặc ADMIN mới được đăng bài!"
                    ));
        }

        return ResponseEntity.ok(
                postService.createPost(post)
        );
    }

    // GET ALL - ai cũng xem được
    @GetMapping
    public ResponseEntity<?> getAllPosts() {
        return ResponseEntity.ok(
                postService.getAllPosts()
        );
    }

    // UPDATE
    // AUTHOR chỉ sửa bài của mình, ADMIN sửa tất cả, USER bị chặn
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long id,
            @RequestBody Post post,
            @AuthenticationPrincipal User currentUser
    ) {
        // Nếu là USER thường → chặn
        if (currentUser.getRole() == Role.USER) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "message", "Bạn không có quyền truy cập vào chỗ này. Chỉ AUTHOR hoặc ADMIN mới được sửa bài!"
                    ));
        }

        try {
            return ResponseEntity.ok(
                    postService.updatePost(id, post)
            );
        } catch (RuntimeException e) {
            // AUTHOR cố sửa bài của người khác
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // DELETE - chỉ ADMIN mới được xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "message", "Bạn không có quyền truy cập vào chỗ này. Chỉ ADMIN mới được xóa bài!"
                    ));
        }

        try {
            postService.deletePost(id);
            return ResponseEntity.ok(
                    Map.of("message", "Xóa bài viết thành công")
            );
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}