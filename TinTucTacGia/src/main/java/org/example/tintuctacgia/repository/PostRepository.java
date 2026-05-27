package org.example.tintuctacgia.repository;

import org.example.tintuctacgia.entity.Post;
import org.example.tintuctacgia.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByStatus(PostStatus status, Pageable pageable);
    List<Post> findByUserId(Long userId);
    List<Post> findByUserIdAndStatus(Long userId, PostStatus status);
    List<Post> findByCategoryIdAndStatus(Long categoryId, PostStatus status);
    List<Post> findByTitleContainingIgnoreCaseAndStatus(String keyword, PostStatus status);
    List<Post> findByStatus(PostStatus status);
    Optional<Post> findBySlug(String slug);
    Page<Post> findAll(Pageable pageable);

    // Đếm theo status - dùng cho thống kê
    long countByStatus(PostStatus status);
}