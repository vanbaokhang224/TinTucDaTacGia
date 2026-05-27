package org.example.tintuctacgia.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.example.tintuctacgia.dto.post.PostRequest;
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

    // CREATE - chỉ AUTHOR hoặc ADMIN
    @PostMapping
    public ResponseEntity<?> createPost(
            @Valid @RequestBody PostRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser.getRole() == Role.READER || currentUser.getRole() == Role.EDITOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Chỉ AUTHOR hoặc ADMIN mới được đăng bài!"));
        }
        return ResponseEntity.ok(postService.createPost(request));
    }

    // GET ALL PUBLISHED - công khai
    @GetMapping
    public ResponseEntity<?> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(postService.getAllPosts(page, size));
    }

    // GET BY ID - công khai
    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(postService.getPostById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // GET BY SLUG - công khai
    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> getPostBySlug(@PathVariable String slug) {
        try {
            return ResponseEntity.ok(postService.getPostBySlug(slug));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // SEARCH - công khai, chỉ bài PUBLISHED
    @GetMapping("/search")
    public ResponseEntity<?> searchPosts(@RequestParam String keyword) {
        return ResponseEntity.ok(postService.searchPosts(keyword));
    }

    // GET BY CATEGORY - công khai
    @GetMapping("/by-category/{categoryId}")
    public ResponseEntity<?> getPostsByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(postService.getPostsByCategory(categoryId));
    }

    // GET BY AUTHOR - công khai
    @GetMapping("/by-author/{userId}")
    public ResponseEntity<?> getPostsByAuthor(@PathVariable Long userId) {
        return ResponseEntity.ok(postService.getPostsByAuthor(userId));
    }

    // MY POSTS - tác giả xem bài của mình (cần đăng nhập)
    @GetMapping("/my-posts")
    public ResponseEntity<?> getMyPosts(@AuthenticationPrincipal User currentUser) {
        if (currentUser.getRole() == Role.READER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "READER không có bài viết!"));
        }
        return ResponseEntity.ok(postService.getMyPosts());
    }

    // PENDING REVIEW - EDITOR/ADMIN xem bài chờ duyệt
    @GetMapping("/pending-review")
    public ResponseEntity<?> getPendingReview(@AuthenticationPrincipal User currentUser) {
        if (currentUser.getRole() != Role.EDITOR && currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Chỉ EDITOR hoặc ADMIN mới xem được bài chờ duyệt!"));
        }
        return ResponseEntity.ok(postService.getPostsPendingReview());
    }

    // SUBMIT FOR REVIEW - AUTHOR gửi bài chờ duyệt
    @PatchMapping("/{id}/submit")
    public ResponseEntity<?> submitForReview(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser.getRole() != Role.AUTHOR && currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Chỉ AUTHOR mới được gửi bài duyệt!"));
        }
        try {
            return ResponseEntity.ok(postService.submitForReview(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // APPROVE - EDITOR/ADMIN duyệt bài
    @PatchMapping("/{id}/approve")
    public ResponseEntity<?> approvePost(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser.getRole() != Role.EDITOR && currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Chỉ EDITOR hoặc ADMIN mới được duyệt bài!"));
        }
        try {
            return ResponseEntity.ok(postService.approvePost(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // REJECT - EDITOR/ADMIN từ chối bài
    @PatchMapping("/{id}/reject")
    public ResponseEntity<?> rejectPost(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser.getRole() != Role.EDITOR && currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Chỉ EDITOR hoặc ADMIN mới được từ chối bài!"));
        }
        try {
            String reason = body.getOrDefault("reason", "Không có lý do");
            return ResponseEntity.ok(postService.rejectPost(id, reason));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // UPDATE - AUTHOR sửa bài mình, EDITOR/ADMIN sửa tất cả
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser.getRole() == Role.READER) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "READER không có quyền sửa bài!"));
        }
        try {
            return ResponseEntity.ok(postService.updatePost(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // DELETE - ADMIN và EDITOR
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.EDITOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Chỉ ADMIN hoặc EDITOR mới được xóa bài!"));
        }
        try {
            postService.deletePost(id);
            return ResponseEntity.ok(Map.of("message", "Xóa bài viết thành công"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}