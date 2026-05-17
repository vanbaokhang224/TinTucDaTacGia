package org.example.tintuctacgia.service;

import lombok.RequiredArgsConstructor;

import org.example.tintuctacgia.entity.Post;
import org.example.tintuctacgia.entity.User;
import org.example.tintuctacgia.enums.Role;
import org.example.tintuctacgia.exception.PostNotFoundException;
import org.example.tintuctacgia.repository.PostRepository;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    // Lấy user đang đăng nhập từ SecurityContext
    private User getCurrentUser() {
        return (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    // CREATE POST
    public Post createPost(Post post) {
        post.setUser(getCurrentUser());
        return postRepository.save(post);
    }

    // GET ALL POSTS
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // GET POST BY ID
    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
    }

    // UPDATE POST
    public Post updatePost(Long id, Post updatedPost) {

        // FIX: Dùng PostNotFoundException thay vì RuntimeException
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));

        User currentUser = getCurrentUser();

        // Kiểm tra quyền: chỉ ADMIN hoặc chính tác giả mới được sửa
        if (currentUser.getRole() != Role.ADMIN
                && !post.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền sửa bài này");
        }

        // FIX: Update đủ 3 field (trước chỉ có title và content)
        post.setTitle(updatedPost.getTitle());
        post.setContent(updatedPost.getContent());
        post.setCategory(updatedPost.getCategory());

        return postRepository.save(post);
    }

    // DELETE POST
    public void deletePost(Long id) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));

        User currentUser = getCurrentUser();

        // Chỉ ADMIN hoặc chính tác giả mới được xóa
        if (currentUser.getRole() != Role.ADMIN
                && !post.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền xóa bài này");
        }

        postRepository.delete(post);
    }
}