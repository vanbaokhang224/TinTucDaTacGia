package org.example.tintuctacgia.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "authors")
@DiscriminatorValue("AUTHOR")
public class Author extends User {

    @Column(length = 500)
    private String bio;

    // Chuyên ngành viết (Công nghệ, Thể thao, Kinh tế,...)
    private String specialty;

    // Tổng số bài đã viết - tính tự động
    @Column(name = "total_posts", columnDefinition = "int default 0")
    private int totalPosts = 0;
}

