package org.example.tintuctacgia.repository;

import org.example.tintuctacgia.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository
        extends JpaRepository<Post, Long> {
}
