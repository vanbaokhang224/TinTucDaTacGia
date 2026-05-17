package org.example.tintuctacgia.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt;

    // COMMENT thuộc USER
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // COMMENT thuộc POST
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @PrePersist
    public void createdAt() {

        this.createdAt = LocalDateTime.now();
    }
}
