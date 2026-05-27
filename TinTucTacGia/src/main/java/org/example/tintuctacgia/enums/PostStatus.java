package org.example.tintuctacgia.enums;

public enum PostStatus {
    DRAFT,      // Bản nháp - AUTHOR đang viết
    REVIEW,     // Chờ duyệt - AUTHOR gửi lên
    PUBLISHED,  // Đã xuất bản - EDITOR/ADMIN duyệt
    REJECTED    // Bị từ chối - EDITOR/ADMIN từ chối
}
