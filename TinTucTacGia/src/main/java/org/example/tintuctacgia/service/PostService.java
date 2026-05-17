package org.example.tintuctacgia.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tintuctacgia.entity.Post;
import org.example.tintuctacgia.entity.User;
import org.example.tintuctacgia.exception.PostNotFoundException;
import org.example.tintuctacgia.repository.PostRepository;
import org.example.tintuctacgia.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;

    private final PostRepository postRepository;

    // CREATE POST
    @Transactional
    public Post createPost(Post post, String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow();

        post.setUser(user);

        return postRepository.save(post);
    }

    // GET ALL POSTS
    public List<Post> getAllPosts() {

        return postRepository.findAll();
    }

    // Thêm GET POST BY ID (cần thiết cho trang chi tiết bài viết)
    public Post getPostById(Long id) {

        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
    }

    // UPDATE POST
    @Transactional
    public Post updatePost(
            Long id,
            Post updatedPost,
            String email
    ) {

        Post post = postRepository.findById(id)
                .orElseThrow();

        User currentUser = userRepository
                .findByEmail(email)
                .orElseThrow();

        // ADMIN được sửa tất cả
        if (currentUser.getRole().name().equals("ADMIN")) {

        }

        // AUTHOR chỉ sửa bài mình
        else if (
                !post.getUser().getId()
                        .equals(currentUser.getId())
        ) {

            throw new AccessDeniedException(
                    "Bạn không có quyền sửa bài này"
            );
        }

        post.setTitle(updatedPost.getTitle());

        post.setContent(updatedPost.getContent());

        post.setCategory(updatedPost.getCategory());

        return postRepository.save(post);
    }

    // DELETE POST
    @Transactional
    public void deletePost(Long id, String email) {

        Post post = postRepository.findById(id)
                .orElseThrow();

        User currentUser = userRepository
                .findByEmail(email)
                .orElseThrow();

        // ADMIN xóa tất cả
        if (currentUser.getRole().name().equals("ADMIN")) {

        }

        // AUTHOR chỉ xóa bài mình
        else if (
                !post.getUser().getId()
                        .equals(currentUser.getId())
        ) {

            throw new AccessDeniedException(
                    "Bạn không có quyền xóa bài này"
            );
        }

        postRepository.delete(post);
    }
}
