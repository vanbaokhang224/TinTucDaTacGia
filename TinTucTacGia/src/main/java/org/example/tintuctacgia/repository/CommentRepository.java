package org.example.tintuctacgia.repository;

import org.example.tintuctacgia.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    //  lấy comment theo bài viết (dùng trong CommentService)
    List<Comment> findByPostId(Long postId);
}