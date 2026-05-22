package org.example.tintuctacgia.service;

import lombok.RequiredArgsConstructor;

import org.example.tintuctacgia.dto.PostRequest;
import org.example.tintuctacgia.dto.PostResponse;
import org.example.tintuctacgia.entity.Post;
import org.example.tintuctacgia.entity.User;
import org.example.tintuctacgia.enums.Role;
import org.example.tintuctacgia.exception.PostNotFoundException;
import org.example.tintuctacgia.mapper.PostMapper;
import org.example.tintuctacgia.repository.PostRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
    public PostResponse createPost(PostRequest request) {
        User currentUser = getCurrentUser();

        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCategory(request.getCategory());
        post.setUser(currentUser);

        return PostMapper.toResponse(postRepository.save(post));
    }

    // GET ALL POSTS (phân trang, mới nhất trước)
    public Page<PostResponse> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(
                page, size,
                Sort.by("createdAt").descending()
        );
        return postRepository.findAll(pageable)
                .map(PostMapper::toResponse);
    }

    // GET POST BY ID
    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
        return PostMapper.toResponse(post);
    }

    // SEARCH POSTS BY TITLE
    public List<PostResponse> searchPosts(String keyword) {
        return postRepository
                .findByTitleContainingIgnoreCase(keyword)
                .stream()
                .map(PostMapper::toResponse)
                .collect(Collectors.toList());
    }

    // GET POSTS BY CATEGORY
    public List<PostResponse> getPostsByCategory(String category) {
        return postRepository.findByCategory(category)
                .stream()
                .map(PostMapper::toResponse)
                .collect(Collectors.toList());
    }

    // GET POSTS BY AUTHOR
    public List<PostResponse> getPostsByAuthor(Long userId) {
        return postRepository.findByUserId(userId)
                .stream()
                .map(PostMapper::toResponse)
                .collect(Collectors.toList());
    }

    // UPDATE POST
    public PostResponse updatePost(Long id, PostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));

        User currentUser = getCurrentUser();

        if (currentUser.getRole() != Role.ADMIN
                && !post.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền sửa bài này");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setCategory(request.getCategory());

        return PostMapper.toResponse(postRepository.save(post));
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
}