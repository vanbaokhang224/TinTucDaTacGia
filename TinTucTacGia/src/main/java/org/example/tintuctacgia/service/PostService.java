package org.example.tintuctacgia.service;

import lombok.RequiredArgsConstructor;

import org.example.tintuctacgia.dto.post.PostRequest;
import org.example.tintuctacgia.dto.post.PostResponse;
import org.example.tintuctacgia.entity.*;
import org.example.tintuctacgia.enums.PostStatus;
import org.example.tintuctacgia.enums.Role;
import org.example.tintuctacgia.exception.PostNotFoundException;
import org.example.tintuctacgia.exception.UnauthorizedException;
import org.example.tintuctacgia.mapper.PostMapper;
import org.example.tintuctacgia.repository.CategoryRepository;
import org.example.tintuctacgia.repository.PostRepository;
import org.example.tintuctacgia.repository.TagRepository;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    private User getCurrentUser() {
        return (User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

    // Tạo slug từ title
    private String generateSlug(String title) {
        String slug = Normalizer.normalize(title, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        // Thêm timestamp để tránh trùng
        return slug + "-" + System.currentTimeMillis();
    }

    // CREATE POST - mặc định DRAFT
    @CacheEvict(value = {"posts", "postsAll"}, allEntries = true)
    public PostResponse createPost(PostRequest request) {
        User currentUser = getCurrentUser();

        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setSlug(generateSlug(request.getTitle()));
        post.setContent(request.getContent());
        post.setThumbnail(request.getThumbnail());
        post.setStatus(PostStatus.DRAFT);
        post.setUser(currentUser);

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
            post.setCategory(category);
        }

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<Tag> tags = tagRepository.findByIdIn(request.getTagIds());
            post.setTags(tags);
        }

        return PostMapper.toResponse(postRepository.save(post));
    }

    // GET ALL PUBLISHED - công khai
    @Cacheable(value = "postsAll", key = "#page + '-' + #size")
    public Page<PostResponse> getAllPosts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return postRepository.findByStatus(PostStatus.PUBLISHED, pageable)
                .map(PostMapper::toResponse);
    }

    // GET POST BY ID - chỉ trả PUBLISHED cho public
    @Cacheable(value = "posts", key = "#id")
    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
        return PostMapper.toResponse(post);
    }

    // GET POST BY SLUG
    @Cacheable(value = "posts", key = "#slug")
    public PostResponse getPostBySlug(String slug) {
        Post post = postRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết"));
        return PostMapper.toResponse(post);
    }

    // SEARCH - chỉ tìm bài PUBLISHED
    public List<PostResponse> searchPosts(String keyword) {
        return postRepository
                .findByTitleContainingIgnoreCaseAndStatus(keyword, PostStatus.PUBLISHED)
                .stream().map(PostMapper::toResponse).collect(Collectors.toList());
    }

    // GET BY CATEGORY - chỉ bài PUBLISHED
    public List<PostResponse> getPostsByCategory(Long categoryId) {
        return postRepository.findByCategoryIdAndStatus(categoryId, PostStatus.PUBLISHED)
                .stream().map(PostMapper::toResponse).collect(Collectors.toList());
    }

    // GET BY AUTHOR
    public List<PostResponse> getPostsByAuthor(Long userId) {
        return postRepository.findByUserId(userId)
                .stream().map(PostMapper::toResponse).collect(Collectors.toList());
    }

    // MY POSTS - tác giả xem bài của mình (tất cả status)
    public List<PostResponse> getMyPosts() {
        User currentUser = getCurrentUser();
        return postRepository.findByUserId(currentUser.getId())
                .stream().map(PostMapper::toResponse).collect(Collectors.toList());
    }

    // GET POSTS PENDING REVIEW - EDITOR/ADMIN xem bài chờ duyệt
    public List<PostResponse> getPostsPendingReview() {
        return postRepository.findByStatus(PostStatus.REVIEW)
                .stream().map(PostMapper::toResponse).collect(Collectors.toList());
    }

    // SUBMIT FOR REVIEW - AUTHOR gửi bài chờ duyệt
    public PostResponse submitForReview(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
        User currentUser = getCurrentUser();

        if (!post.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Bạn không có quyền gửi bài này");
        }
        if (post.getStatus() != PostStatus.DRAFT && post.getStatus() != PostStatus.REJECTED) {
            throw new RuntimeException("Chỉ có thể gửi duyệt bài ở trạng thái DRAFT hoặc REJECTED");
        }

        post.setStatus(PostStatus.REVIEW);
        return PostMapper.toResponse(postRepository.save(post));
    }

    // APPROVE - EDITOR/ADMIN duyệt bài
    @CacheEvict(value = {"posts", "postsAll"}, allEntries = true)
    public PostResponse approvePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));

        if (post.getStatus() != PostStatus.REVIEW) {
            throw new RuntimeException("Chỉ có thể duyệt bài ở trạng thái REVIEW");
        }

        User currentUser = getCurrentUser();
        post.setStatus(PostStatus.PUBLISHED);
        post.setReviewedBy(currentUser);
        post.setPublishedAt(LocalDateTime.now());
        return PostMapper.toResponse(postRepository.save(post));
    }

    // REJECT - EDITOR/ADMIN từ chối bài
    @CacheEvict(value = {"posts", "postsAll"}, allEntries = true)
    public PostResponse rejectPost(Long id, String reason) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));

        if (post.getStatus() != PostStatus.REVIEW) {
            throw new RuntimeException("Chỉ có thể từ chối bài ở trạng thái REVIEW");
        }

        User currentUser = getCurrentUser();
        post.setStatus(PostStatus.REJECTED);
        post.setRejectedReason(reason);
        post.setReviewedBy(currentUser);
        return PostMapper.toResponse(postRepository.save(post));
    }

    // UPDATE POST - AUTHOR sửa bài của mình (chỉ DRAFT/REJECTED)
    @CacheEvict(value = {"posts", "postsAll"}, allEntries = true)
    public PostResponse updatePost(Long id, PostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
        User currentUser = getCurrentUser();

        if (currentUser.getRole() != Role.ADMIN
                && currentUser.getRole() != Role.EDITOR
                && !post.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Bạn không có quyền sửa bài này");
        }

        // Logic Sửa bài của AUTHOR
        if (currentUser.getRole() == Role.AUTHOR) {
            if (post.getStatus() == PostStatus.REVIEW) {
                throw new RuntimeException("Bài viết đang chờ duyệt, bạn không thể sửa lúc này!");
            }
            // Tác giả sửa bài đã đăng -> Tự động chuyển về trạng thái Chờ Duyệt (REVIEW)
            if (post.getStatus() == PostStatus.PUBLISHED) {
                post.setStatus(PostStatus.REVIEW);
            }
        }

        post.setTitle(request.getTitle());
        post.setSlug(generateSlug(request.getTitle()));
        post.setContent(request.getContent());
        
        if (request.getThumbnail() != null) {
            post.setThumbnail(request.getThumbnail());
        }

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
            post.setCategory(category);
        }

        if (request.getTagIds() != null) {
            List<Tag> tags = tagRepository.findByIdIn(request.getTagIds());
            post.setTags(tags);
        }

        return PostMapper.toResponse(postRepository.save(post));
    }

    // DELETE POST
    @CacheEvict(value = {"posts", "postsAll"}, allEntries = true)
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id));
        User currentUser = getCurrentUser();

        if (currentUser.getRole() != Role.ADMIN
                && currentUser.getRole() != Role.EDITOR
                && !post.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Bạn không có quyền xóa bài này");
        }
        postRepository.delete(post);
    }
}