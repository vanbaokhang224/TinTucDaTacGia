package org.example.tintuctacgia.controller;

import lombok.RequiredArgsConstructor;
import org.example.tintuctacgia.entity.Comment;
import org.example.tintuctacgia.entity.Post;
import org.example.tintuctacgia.entity.User;
import org.example.tintuctacgia.repository.PostRepository;
import org.example.tintuctacgia.repository.UserRepository;
import org.example.tintuctacgia.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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

    // GET COMMENTS
    @GetMapping
    public ResponseEntity<?> getComments() {

        return ResponseEntity.ok(
                commentService.getComments()
        );
    }

    // DELETE COMMENT
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Long id
    ) {

        commentService.deleteComment(id);

        return ResponseEntity.ok(
                "Comment deleted"
        );
    }
}
