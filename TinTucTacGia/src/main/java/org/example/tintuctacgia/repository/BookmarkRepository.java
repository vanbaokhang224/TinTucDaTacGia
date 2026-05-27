package org.example.tintuctacgia.repository;

import org.example.tintuctacgia.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByUserIdAndPostId(Long userId, Long postId);
    List<Bookmark> findByUserId(Long userId);
    long countByPostId(Long postId);

    // Đếm bookmark theo danh sách postId - dùng cho thống kê
    long countByPostIdIn(List<Long> postIds);

    @Transactional
    void deleteByUserIdAndPostId(Long userId, Long postId);
}
