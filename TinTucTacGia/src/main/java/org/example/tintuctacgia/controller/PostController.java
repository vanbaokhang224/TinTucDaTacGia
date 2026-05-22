package org.example.tintuctacgia.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.example.tintuctacgia.dto.PostRequest;
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

    // CREATE - chỉ ADMIN hoặc AUTHOR
    @PostMapping
    public ResponseEntity<?> createPost(
            @Valid @RequestBody PostRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser.getRole() == Role.USER) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message",
                            "Bạn không có quyền đăng bài. Chỉ AUTHOR hoặc ADMIN mới được đăng!"));
        }
        return ResponseEntity.ok(postService.createPost(request));
    }

    // GET ALL - phân trang, ai cũng xem được
    @GetMapping
    public ResponseEntity<?> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(postService.getAllPosts(page, size));
    }

    // SEARCH - tìm theo title, ai cũng xem được
    @GetMapping("/search")
    public ResponseEntity<?> searchPosts(
            @RequestParam String keyword
    ) {
        return ResponseEntity.ok(postService.searchPosts(keyword));
    }

    // GET BY CATEGORY - ai cũng xem được
    @GetMapping("/by-category/{category}")
    public ResponseEntity<?> getPostsByCategory(
            @PathVariable String category
    ) {
        return ResponseEntity.ok(postService.getPostsByCategory(category));
    }

    // GET BY AUTHOR - công khai, ai cũng xem được
    // VD: GET /api/posts/by-author/1
    @GetMapping("/by-author/{userId}")
    public ResponseEntity<?> getPostsByAuthor(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(postService.getPostsByAuthor(userId));
    }

    // MY POSTS - tác giả xem bài của chính mình (cần đăng nhập)
    // VD: GET /api/posts/my-posts
    @GetMapping("/my-posts")
    public ResponseEntity<?> getMyPosts(
            @AuthenticationPrincipal User currentUser
    ) {
        // Chỉ AUTHOR và ADMIN mới có bài viết
        if (currentUser.getRole() == Role.USER) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message",
                            "Bạn không có bài viết nào. Chỉ AUTHOR mới có thể đăng bài!"));
        }
        return ResponseEntity.ok(
                postService.getPostsByAuthor(currentUser.getId())
        );
    }

    // GET BY ID - ai cũng xem được
    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(
            @PathVariable Long id
    ) {
        try {
            return ResponseEntity.ok(postService.getPostById(id));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // UPDATE - AUTHOR chỉ sửa bài của mình, ADMIN sửa tất cả
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser.getRole() == Role.USER) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message",
                            "Bạn không có quyền sửa bài. Chỉ AUTHOR hoặc ADMIN mới được sửa!"));
        }
        try {
            return ResponseEntity.ok(postService.updatePost(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // DELETE - chỉ ADMIN
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message",
                            "Bạn không có quyền xóa bài. Chỉ ADMIN mới được xóa!"));
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