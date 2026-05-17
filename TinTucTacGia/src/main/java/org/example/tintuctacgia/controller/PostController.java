package org.example.tintuctacgia.controller;

import lombok.RequiredArgsConstructor;
import org.example.tintuctacgia.entity.Post;
import org.example.tintuctacgia.repository.UserRepository;
import org.example.tintuctacgia.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor

public class PostController {

    private final PostService postService;

    @Autowired
    private UserRepository userRepository;

    // CREATE
    @PostMapping
    public ResponseEntity<?> createPost(
            @RequestBody Post post,
            Principal principal
    ) {

        Post createdPost =
                postService.createPost(post, principal.getName());

        return ResponseEntity.ok(createdPost);
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<?> getAllPosts() {

        return ResponseEntity.ok(
                postService.getAllPosts()
        );
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePost(
            @PathVariable Long id,
            @RequestBody Post post,
            Principal principal
    ) {

        return ResponseEntity.ok(
                postService.updatePost(
                        id,
                        post,
                        principal.getName()
                )
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(
            @PathVariable Long id,
            Principal principal
    ) {

        postService.deletePost(
                id,
                principal.getName()
        );

        return ResponseEntity.ok("Deleted");
    }

}
