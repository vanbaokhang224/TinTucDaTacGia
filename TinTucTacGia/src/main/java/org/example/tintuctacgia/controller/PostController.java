package org.example.tintuctacgia.controller;

import lombok.RequiredArgsConstructor;

import org.example.tintuctacgia.entity.Post;
import org.example.tintuctacgia.entity.User;
import org.example.tintuctacgia.service.PostService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // CREATE
    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestBody Post post
    ) {
        return ResponseEntity.ok(
                postService.createPost(post)
        );
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<?> getAllPosts() {
        return ResponseEntity.ok(
                postService.getAllPosts()
        );
    }

    // UPDATE
    // FIX: Thêm @AuthenticationPrincipal để lấy user đang đăng nhập
    //      Trả về 403 đúng chuẩn HTTP thay vì throw RuntimeException không bắt được
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long id,
            @RequestBody Post post,
            @AuthenticationPrincipal User currentUser
    ) {
        try {
            return ResponseEntity.ok(
                    postService.updatePost(id, post)
            );
        } catch (RuntimeException e) {
            // PostService đã check quyền, nếu không có quyền thì trả 403
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(
            @PathVariable Long id
    ) {
        try {
            postService.deletePost(id);
            return ResponseEntity.ok("Deleted");
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());
        }
    }
}