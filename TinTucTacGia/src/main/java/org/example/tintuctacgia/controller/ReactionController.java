package org.example.tintuctacgia.controller;

import lombok.RequiredArgsConstructor;
import org.example.tintuctacgia.entity.User;
import org.example.tintuctacgia.enums.ReactionType;
import org.example.tintuctacgia.service.ReactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reactions")
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;

    // LIKE - POST /api/reactions/posts/1/like
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<?> like(
            @PathVariable Long postId,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(
                reactionService.react(postId, ReactionType.LIKE, currentUser)
        );
    }

    // DISLIKE - POST /api/reactions/posts/1/dislike
    @PostMapping("/posts/{postId}/dislike")
    public ResponseEntity<?> dislike(
            @PathVariable Long postId,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(
                reactionService.react(postId, ReactionType.DISLIKE, currentUser)
        );
    }

    // GET reaction info - GET /api/reactions/posts/1
    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> getReactionInfo(
            @PathVariable Long postId,
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(
                reactionService.getReactionInfo(postId, currentUser)
        );
    }
}
