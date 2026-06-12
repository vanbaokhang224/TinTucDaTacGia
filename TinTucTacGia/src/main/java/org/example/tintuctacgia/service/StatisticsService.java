package org.example.tintuctacgia.service;

import lombok.RequiredArgsConstructor;

import org.example.tintuctacgia.dto.AuthorStatisticsResponse;
import org.example.tintuctacgia.dto.StatisticsResponse;
import org.example.tintuctacgia.entity.User;
import org.example.tintuctacgia.enums.PostStatus;
import org.example.tintuctacgia.enums.ReactionType;
import org.example.tintuctacgia.enums.Role;
import org.example.tintuctacgia.exception.UserNotFoundException;
import org.example.tintuctacgia.repository.*;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReactionRepository reactionRepository;
    private final BookmarkRepository bookmarkRepository;

    // THỐNG KÊ TỔNG QUAN - chỉ ADMIN
    public StatisticsResponse getSystemStatistics() {
        long totalUsers = userRepository.count();
        long totalReaders = userRepository.countByRole(Role.READER);
        long totalAuthors = userRepository.countByRole(Role.AUTHOR);
        long totalEditors = userRepository.countByRole(Role.EDITOR);
        long totalPosts = postRepository.count();
        long totalPublished = postRepository.countByStatus(PostStatus.PUBLISHED);
        long totalDraft = postRepository.countByStatus(PostStatus.DRAFT);
        long totalPending = postRepository.countByStatus(PostStatus.REVIEW);
        long totalComments = commentRepository.count();
        long totalReactions = reactionRepository.count();
        long totalBookmarks = bookmarkRepository.count();

        return StatisticsResponse.builder()
                .totalUsers(totalUsers)
                .totalReaders(totalReaders)
                .totalAuthors(totalAuthors)
                .totalEditors(totalEditors)
                .totalPosts(totalPosts)
                .totalPublishedPosts(totalPublished)
                .totalDraftPosts(totalDraft)
                .totalPendingPosts(totalPending)
                .totalComments(totalComments)
                .totalReactions(totalReactions)
                .totalBookmarks(totalBookmarks)
                .build();
    }

    // THỐNG KÊ CỦA 1 TÁC GIẢ
    public AuthorStatisticsResponse getAuthorStatistics(Long authorId) {
        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException(authorId));

        // Lấy tất cả bài của author
        var posts = postRepository.findByUserId(authorId, Pageable.unpaged()).getContent();
        List<Long> postIds = posts.stream()
                .map(p -> p.getId())
                .collect(Collectors.toList());

        long totalPosts = posts.size();
        long totalPublished = posts.stream()
                .filter(p -> p.getStatus() == PostStatus.PUBLISHED).count();
        long totalDraft = posts.stream()
                .filter(p -> p.getStatus() == PostStatus.DRAFT).count();
        long totalRejected = posts.stream()
                .filter(p -> p.getStatus() == PostStatus.REJECTED).count();
        long totalPending = posts.stream()
                .filter(p -> p.getStatus() == PostStatus.REVIEW).count();

        // Tổng comment, like, bookmark trên tất cả bài của author
        long totalComments = postIds.isEmpty() ? 0 :
                commentRepository.countByPostIdIn(postIds);
        long totalLikes = postIds.isEmpty() ? 0 :
                reactionRepository.countByPostIdInAndType(postIds, ReactionType.LIKE);
        long totalBookmarks = postIds.isEmpty() ? 0 :
                bookmarkRepository.countByPostIdIn(postIds);

        return AuthorStatisticsResponse.builder()
                .authorId(authorId)
                .authorName(user.getName())
                .totalPosts(totalPosts)
                .totalPublished(totalPublished)
                .totalDraft(totalDraft)
                .totalRejected(totalRejected)
                .totalPending(totalPending)
                .totalComments(totalComments)
                .totalLikes(totalLikes)
                .totalBookmarks(totalBookmarks)
                .build();
    }

    // THỐNG KÊ CỦA CHÍNH MÌNH (AUTHOR tự xem)
    public AuthorStatisticsResponse getMyStatistics(User currentUser) {
        return getAuthorStatistics(currentUser.getId());
    }
}
