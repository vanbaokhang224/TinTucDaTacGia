package org.example.tintuctacgia.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponse {

    // Thống kê tổng quan hệ thống (ADMIN)
    private long totalUsers;
    private long totalReaders;
    private long totalAuthors;
    private long totalEditors;
    private long totalPosts;
    private long totalPublishedPosts;
    private long totalDraftPosts;
    private long totalPendingPosts;
    private long totalComments;
    private long totalReactions;
    private long totalBookmarks;
}
