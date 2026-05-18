package org.example.tintuctacgia.service;

import lombok.RequiredArgsConstructor;

import org.example.tintuctacgia.entity.Post;
import org.example.tintuctacgia.entity.User;
import org.example.tintuctacgia.enums.Role;
import org.example.tintuctacgia.exception.PostNotFoundException;
import org.example.tintuctacgia.repository.PostRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    // Lấy user đang đăng nhập
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

    // GET ALL POSTS (có phân trang)
    // page: số trang (bắt đầu từ 0), size: số bài mỗi trang
    public Page<Post> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("createdAt").descending()
        );
        return postRepository.findAll(pageable);
    }

    // GET POST BY ID
    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
    }

    // UPDATE POST
    public Post updatePost(Long id, Post updatedPost) {

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));

        User currentUser = getCurrentUser();

        if (currentUser.getRole() != Role.ADMIN
                && !post.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền sửa bài này");
        }

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

        if (currentUser.getRole() != Role.ADMIN
                && !post.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền xóa bài này");
        }

        postRepository.delete(post);
    }

    // GET POSTS BY AUTHOR
    public List<Post> getPostsByAuthor(Long userId) {
        return postRepository.findByUserId(userId);
    }

    // GET POSTS BY CATEGORY
    public List<Post> getPostsByCategory(String category) {
        return postRepository.findByCategory(category);
    }

    // SEARCH POSTS BY TITLE
    public List<Post> searchPosts(String keyword) {
        return postRepository
                .findByTitleContainingIgnoreCase(keyword);
    }
}