package org.example.tintuctacgia.repository;

import org.example.tintuctacgia.entity.Reaction;
import org.example.tintuctacgia.enums.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Optional<Reaction> findByUserIdAndPostId(Long userId, Long postId);
    long countByPostIdAndType(Long postId, ReactionType type);

    // Đếm reaction theo danh sách postId - dùng cho thống kê
    long countByPostIdInAndType(List<Long> postIds, ReactionType type);

    @Transactional
    void deleteByUserIdAndPostId(Long userId, Long postId);
}
