package org.example.tintuctacgia.controller;

import lombok.RequiredArgsConstructor;
import org.example.tintuctacgia.entity.User;
import org.example.tintuctacgia.service.BookmarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    // TOGGLE BOOKMARK - POST /api/bookmarks/posts/1
    @PostMapping("/posts/{postId}")
    public ResponseEntity<?> toggleBookmark(
            @PathVariable Long postId,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(
                bookmarkService.toggleBookmark(postId, currentUser)
        );
    }

    // GET MY BOOKMARKS - GET /api/bookmarks/my
    @GetMapping("/my")
    public ResponseEntity<?> getMyBookmarks(
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(
                bookmarkService.getMyBookmarks(currentUser)
        );
    }
}