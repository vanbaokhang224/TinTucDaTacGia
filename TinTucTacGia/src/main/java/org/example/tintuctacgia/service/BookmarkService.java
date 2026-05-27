package org.example.tintuctacgia.service;

import lombok.RequiredArgsConstructor;
import org.example.tintuctacgia.dto.reaction.BookmarkResponse;
import org.example.tintuctacgia.entity.Bookmark;
import org.example.tintuctacgia.entity.Post;
import org.example.tintuctacgia.entity.User;
import org.example.tintuctacgia.exception.PostNotFoundException;
import org.example.tintuctacgia.repository.BookmarkRepository;
import org.example.tintuctacgia.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final PostRepository postRepository;

    @Transactional
    public Map<String, Object> toggleBookmark(Long postId, User currentUser) {
        postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        boolean alreadyBookmarked = bookmarkRepository
                .findByUserIdAndPostId(currentUser.getId(), postId)
                .isPresent();

        if (alreadyBookmarked) {
            bookmarkRepository.deleteByUserIdAndPostId(currentUser.getId(), postId);
            return Map.of("message", "Đã bỏ bookmark", "bookmarked", false);
        } else {
            Post post = postRepository.findById(postId).get();
            Bookmark bookmark = new Bookmark();
            bookmark.setUser(currentUser);
            bookmark.setPost(post);
            bookmarkRepository.save(bookmark);
            return Map.of("message", "Đã bookmark bài viết", "bookmarked", true);
        }
    }

    public List<BookmarkResponse> getMyBookmarks(User currentUser) {
        return bookmarkRepository.findByUserId(currentUser.getId())
                .stream()
                .map(b -> BookmarkResponse.builder()
                        .id(b.getId())
                        .postId(b.getPost().getId())
                        .postTitle(b.getPost().getTitle())
                        .postSlug(b.getPost().getSlug())
                        .createdAt(b.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
