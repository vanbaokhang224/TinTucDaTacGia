package org.example.tintuctacgia.repository;

import org.example.tintuctacgia.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // Lấy bài viết theo tác giả
    List<Post> findByUserId(Long userId);

    // Lấy bài viết theo danh mục
    List<Post> findByCategory(String category);

    // Tìm kiếm theo title (không phân biệt hoa thường)
    List<Post> findByTitleContainingIgnoreCase(String keyword);

    // Phân trang (Spring Data tự xử lý khi truyền Pageable)
    Page<Post> findAll(Pageable pageable);
}
