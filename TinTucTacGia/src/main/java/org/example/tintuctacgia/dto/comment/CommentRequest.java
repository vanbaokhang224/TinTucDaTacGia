package org.example.tintuctacgia.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequest {

    @NotBlank(message = "Nội dung comment không được để trống")
    @Size(min = 1, max = 1000, message = "Comment phải từ 1 đến 1000 ký tự")
    private String content;
}
