package org.example.tintuctacgia.service;

import lombok.RequiredArgsConstructor;

import org.example.tintuctacgia.entity.Post;
import org.example.tintuctacgia.entity.User;
import org.example.tintuctacgia.enums.Role;
import org.example.tintuctacgia.repository.PostRepository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    // CREATE POST
    public Post createPost(Post post) {

        User currentUser =
                (User) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        post.setUser(currentUser);

        return postRepository.save(post);
    }

    // GET ALL POSTS
    public List<Post> getAllPosts() {

        return postRepository.findAll();
    }

    // UPDATE POST
    public Post updatePost(
            Long id,
            Post updatedPost
    ) {

        Post post =
                postRepository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Post not found"
                                )
                        );

        User currentUser =
                (User) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        // CHECK OWNER
        if (
                !post.getUser().getId()
                        .equals(currentUser.getId())
                        &&
                        currentUser.getRole() != Role.ADMIN
        ) {

            throw new RuntimeException(
                    "Bạn không có quyền sửa bài này"
            );
        }

        post.setTitle(
                updatedPost.getTitle()
        );

        post.setContent(
                updatedPost.getContent()
        );

        return postRepository.save(post);
    }

    // DELETE POST
    public void deletePost(Long id) {

        Post post =
                postRepository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Post not found"
                                )
                        );

        User currentUser =
                (User) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        // CHECK OWNER OR ADMIN
        if (
                !post.getUser().getId()
                        .equals(currentUser.getId())
                        &&
                        currentUser.getRole() != Role.ADMIN
        ) {

            throw new RuntimeException(
                    "Bạn không có quyền xóa bài này"
            );
        }

        postRepository.delete(post);
    }
    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }
}