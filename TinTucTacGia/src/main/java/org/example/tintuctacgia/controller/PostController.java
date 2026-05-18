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

    // CREATE - chỉ ADMIN hoặc AUTHOR
    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestBody Post post,
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser.getRole() == Role.USER) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message",
                            "Bạn không có quyền để vào khu vực này. Chỉ AUTHOR hoặc ADMIN mới được đăng bài!"));
        }
        return ResponseEntity.ok(postService.createPost(post));
    }

    // GET ALL - có phân trang, ai cũng xem được
    // Mặc định: trang 0, mỗi trang 10 bài, sắp xếp mới nhất trước
    @GetMapping
    public ResponseEntity<?> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(postService.getAllPosts(page, size));
    }

    // GET BY ID - ai cũng xem được
    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    // SEARCH - tìm theo title
    // VD: /api/posts/search?keyword=công nghệ
    @GetMapping("/search")
    public ResponseEntity<?> searchPosts(
            @RequestParam String keyword
    ) {
        return ResponseEntity.ok(postService.searchPosts(keyword));
    }

    // GET BY CATEGORY - lọc theo danh mục
    // VD: /api/posts/category/Công nghệ
    @GetMapping("/category/{category}")
    public ResponseEntity<?> getPostsByCategory(
            @PathVariable String category
    ) {
        return ResponseEntity.ok(postService.getPostsByCategory(category));
    }

    // GET BY AUTHOR - xem bài của 1 tác giả
    // VD: /api/posts/author/1
    @GetMapping("/author/{userId}")
    public ResponseEntity<?> getPostsByAuthor(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(postService.getPostsByAuthor(userId));
    }

    // UPDATE - AUTHOR chỉ sửa bài của mình, ADMIN sửa tất cả
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long id,
            @RequestBody Post post,
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser.getRole() == Role.USER) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message",
                            "Bạn không có quyền để vào khu vực này. Chỉ AUTHOR hoặc ADMIN mới được sửa bài!"));
        }
        try {
            return ResponseEntity.ok(postService.updatePost(id, post));
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
                            "Bạn không có quyền để vào khu vực này. Chỉ ADMIN mới được xóa bài!"));
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