package org.example.tintuctacgia.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.tintuctacgia.dto.CommentRequest;
import org.example.tintuctacgia.dto.CommentResponse;
import org.example.tintuctacgia.entity.Comment;
import org.example.tintuctacgia.entity.Post;
import org.example.tintuctacgia.entity.User;
import org.example.tintuctacgia.enums.Role;
import org.example.tintuctacgia.exception.CommentNotFoundException;
import org.example.tintuctacgia.exception.PostNotFoundException;
import org.example.tintuctacgia.mapper.CommentMapper;
import org.example.tintuctacgia.repository.CommentRepository;
import org.example.tintuctacgia.repository.PostRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    // CREATE COMMENT
    @Transactional
    public CommentResponse createComment(
            Long postId,
            CommentRequest request,
            User currentUser
    ) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUser(currentUser);
        comment.setPost(post);

        log.info("User {} creating comment on post {}",
                currentUser.getEmail(), postId);

        return CommentMapper.toResponse(commentRepository.save(comment));
    }

    // GET ALL COMMENTS (dành cho admin)
    public List<CommentResponse> getComments() {
        return commentRepository.findAll()
                .stream()
                .map(CommentMapper::toResponse)
                .collect(Collectors.toList());
    }

    // GET COMMENTS THEO BÀI VIẾT
    public List<CommentResponse> getCommentsByPost(Long postId) {
        return commentRepository.findByPostId(postId)
                .stream()
                .map(CommentMapper::toResponse)
                .collect(Collectors.toList());
    }

    // UPDATE COMMENT - chỉ chủ comment mới được sửa
    @Transactional
    public CommentResponse updateComment(
            Long id,
            CommentRequest request,
            User currentUser
    ) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));

        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException(
                    "Bạn không có quyền chỉnh sửa comment này"
            );
        }

        comment.setContent(request.getContent());

        log.info("User {} updating comment {}", currentUser.getEmail(), id);
        return CommentMapper.toResponse(commentRepository.save(comment));
    }

    // DELETE COMMENT
    // ADMIN xóa tất cả, USER xóa của mình, AUTHOR không được xóa
    @Transactional
    public void deleteComment(Long id, User currentUser) {

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));

        if (currentUser.getRole() == Role.AUTHOR) {
            throw new RuntimeException("AUTHOR không có quyền xóa comment");
        }

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isOwner = comment.getUser().getId()
                .equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new RuntimeException(
                    "Bạn không có quyền xóa comment này"
            );
        }

        log.info("User {} deleting comment {}", currentUser.getEmail(), id);
        commentRepository.deleteById(id);
    }
}