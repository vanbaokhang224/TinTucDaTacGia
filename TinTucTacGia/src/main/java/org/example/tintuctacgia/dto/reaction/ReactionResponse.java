package org.example.tintuctacgia.dto.reaction;

import lombok.*;
import org.example.tintuctacgia.enums.ReactionType;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReactionResponse {
    private Long postId;
    private ReactionType myReaction; // null nếu chưa react
    private long totalLikes;
    private long totalDislikes;
}
