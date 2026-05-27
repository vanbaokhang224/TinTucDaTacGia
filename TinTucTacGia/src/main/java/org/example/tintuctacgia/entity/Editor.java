package org.example.tintuctacgia.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "editors")
@DiscriminatorValue("EDITOR")
public class Editor extends User {

    @Column(length = 500)
    private String bio;

    // Phòng ban phụ trách (Nội dung, Kiểm duyệt,...)
    private String department;

    // Tổng số bài đã duyệt - tính tự động
    @Column(name = "total_approved", columnDefinition = "int default 0")
    private int totalApproved = 0;
}
