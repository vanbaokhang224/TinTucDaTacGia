package org.example.tintuctacgia.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorStatisticsResponse {

    // Thống kê của 1 tác giả
    private Long authorId;
    private String authorName;
    private long totalPosts;
    private long totalPublished;
    private long totalDraft;
    private long totalRejected;
    private long totalPending;
    private long totalComments;   // tổng comment trên bài của author
    private long totalLikes;      // tổng like trên bài của author
    private long totalBookmarks;  // tổng bookmark trên bài của author
}
