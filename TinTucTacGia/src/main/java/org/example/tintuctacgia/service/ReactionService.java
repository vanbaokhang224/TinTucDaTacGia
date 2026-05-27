package org.example.tintuctacgia.service;

import lombok.RequiredArgsConstructor;
import org.example.tintuctacgia.dto.reaction.ReactionResponse;
import org.example.tintuctacgia.entity.Post;
import org.example.tintuctacgia.entity.Reaction;
import org.example.tintuctacgia.entity.User;
import org.example.tintuctacgia.enums.ReactionType;
import org.example.tintuctacgia.exception.PostNotFoundException;
import org.example.tintuctacgia.repository.PostRepository;
import org.example.tintuctacgia.repository.ReactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;

    @Transactional
    public ReactionResponse react(Long postId, ReactionType type, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        Optional<Reaction> existing = reactionRepository
                .findByUserIdAndPostId(currentUser.getId(), postId);

        if (existing.isPresent()) {
            if (existing.get().getType() == type) {
                // Cùng loại → toggle off (bỏ react)
                reactionRepository.deleteByUserIdAndPostId(currentUser.getId(), postId);
            } else {
                // Khác loại → đổi react
                existing.get().setType(type);
                reactionRepository.save(existing.get());
            }
        } else {
            // Chưa react → thêm mới
            Reaction reaction = new Reaction();
            reaction.setType(type);
            reaction.setUser(currentUser);
            reaction.setPost(post);
            reactionRepository.save(reaction);
        }

        return buildReactionResponse(postId, currentUser);
    }

    public ReactionResponse getReactionInfo(Long postId, User currentUser) {
        return buildReactionResponse(postId, currentUser);
    }

    private ReactionResponse buildReactionResponse(Long postId, User currentUser) {
        Optional<Reaction> myReaction = reactionRepository
                .findByUserIdAndPostId(currentUser.getId(), postId);
        return ReactionResponse.builder()
                .postId(postId)
                .myReaction(myReaction.map(Reaction::getType).orElse(null))
                .totalLikes(reactionRepository.countByPostIdAndType(postId, ReactionType.LIKE))
                .totalDislikes(reactionRepository.countByPostIdAndType(postId, ReactionType.DISLIKE))
                .build();
    }
}
