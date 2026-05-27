package org.example.tintuctacgia.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.tintuctacgia.dto.comment.CommentRequest;
import org.example.tintuctacgia.dto.comment.CommentResponse;
import org.example.tintuctacgia.entity.Comment;
import org.example.tintuctacgia.entity.Post;
import org.example.tintuctacgia.entity.User;
import org.example.tintuctacgia.enums.Role;
import org.example.tintuctacgia.exception.CommentNotFoundException;
import org.example.tintuctacgia.exception.PostNotFoundException;
import org.example.tintuctacgia.exception.UnauthorizedException;
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

    // CREATE COMMENT - tất cả role đều được comment
    @Transactional
    public CommentResponse createComment(Long postId, CommentRequest request, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUser(currentUser);
        comment.setPost(post);

        log.info("User {} creating comment on post {}", currentUser.getEmail(), postId);
        return CommentMapper.toResponse(commentRepository.save(comment));
    }

    // GET ALL - dành cho ADMIN
    public List<CommentResponse> getComments() {
        return commentRepository.findAll()
                .stream()
                .map(CommentMapper::toResponse)
                .collect(Collectors.toList());
    }

    // GET BY POST - ai cũng xem được
    public List<CommentResponse> getCommentsByPost(Long postId) {
        return commentRepository.findByPostId(postId)
                .stream()
                .map(CommentMapper::toResponse)
                .collect(Collectors.toList());
    }

    // UPDATE - chỉ chủ comment
    @Transactional
    public CommentResponse updateComment(Long id, CommentRequest request, User currentUser) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));

        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Bạn không có quyền chỉnh sửa comment này");
        }

        comment.setContent(request.getContent());
        log.info("User {} updating comment {}", currentUser.getEmail(), id);
        return CommentMapper.toResponse(commentRepository.save(comment));
    }

    // DELETE
    // FIX: Cập nhật logic theo role mới
    // ADMIN → xóa tất cả
    // EDITOR → xóa tất cả (để kiểm duyệt nội dung)
    // READER/AUTHOR → chỉ xóa comment của mình
    @Transactional
    public void deleteComment(Long id, User currentUser) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isEditor = currentUser.getRole() == Role.EDITOR;
        boolean isOwner = comment.getUser().getId().equals(currentUser.getId());

        if (!isAdmin && !isEditor && !isOwner) {
            throw new UnauthorizedException("Bạn không có quyền xóa comment này");
        }

        log.info("User {} deleting comment {}", currentUser.getEmail(), id);
        commentRepository.deleteById(id);
    }
}