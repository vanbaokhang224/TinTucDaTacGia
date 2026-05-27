package org.example.tintuctacgia.repository;

import org.example.tintuctacgia.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);

    // Đếm comment theo danh sách postId - dùng cho thống kê
    long countByPostIdIn(List<Long> postIds);
}