package org.example.tintuctacgia.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tintuctacgia.entity.Comment;
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

    // GET ALL (giữ lại nếu cần dùng cho admin)
    public List<Comment> getComments() {

        return commentRepository.findAll();
    }

    // ✅ GET COMMENTS THEO BÀI VIẾT (thực tế hơn)
    public List<Comment> getCommentsByPost(Long postId) {

        return commentRepository.findByPostId(postId);
    }

    // DELETE
    @Transactional
    public void deleteComment(Long id) {

        // ✅ Kiểm tra tồn tại trước khi xóa
        if (!commentRepository.existsById(id)) {
            throw new CommentNotFoundException(id);
        }

        log.info("Deleting comment with id: {}", id);
        commentRepository.deleteById(id);
    }
}
