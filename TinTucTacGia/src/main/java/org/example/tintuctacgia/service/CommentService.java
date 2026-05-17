package org.example.tintuctacgia.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.example.tintuctacgia.entity.Comment;
import org.example.tintuctacgia.entity.User;
import org.example.tintuctacgia.enums.Role;
import org.example.tintuctacgia.exception.CommentNotFoundException;
import org.example.tintuctacgia.repository.CommentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    // CREATE
    @Transactional
    public Comment createComment(Comment comment) {
        log.info("Creating comment for post id: {}",
                comment.getPost().getId());
        return commentRepository.save(comment);
    }

    // GET ALL (dành cho admin)
    public List<Comment> getComments() {
        return commentRepository.findAll();
    }

    // GET COMMENTS THEO BÀI VIẾT
    public List<Comment> getCommentsByPost(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    // UPDATE COMMENT
    // Chỉ chủ comment mới được sửa
    @Transactional
    public Comment updateComment(
            Long id,
            Comment updatedComment,
            User currentUser
    ) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));

        boolean isOwner = comment.getUser().getId()
                .equals(currentUser.getId());

        if (!isOwner) {
            throw new RuntimeException(
                    "Bạn không có quyền chỉnh sửa comment này"
            );
        }

        comment.setContent(updatedComment.getContent());

        log.info("Updating comment with id: {}", id);
        return commentRepository.save(comment);
    }

    // DELETE COMMENT
    // FIX: AUTHOR không được xóa comment
    // Chỉ ADMIN hoặc chính chủ comment (USER) mới xóa được
    @Transactional
    public void deleteComment(Long id, User currentUser) {

        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));

        // AUTHOR bị chặn hoàn toàn, kể cả xóa comment của chính mình
        if (currentUser.getRole() == Role.AUTHOR) {
            throw new RuntimeException(
                    "AUTHOR không có quyền xóa comment"
            );
        }

        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isOwner = comment.getUser().getId()
                .equals(currentUser.getId());

        // Không phải ADMIN và không phải chủ comment → chặn
        if (!isAdmin && !isOwner) {
            throw new RuntimeException(
                    "Bạn không có quyền xóa comment này"
            );
        }

        log.info("Deleting comment with id: {}", id);
        commentRepository.deleteById(id);
    }
}