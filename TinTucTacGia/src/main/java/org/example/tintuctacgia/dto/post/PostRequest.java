package org.example.tintuctacgia.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class PostRequest {

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(min = 5, max = 255, message = "Tiêu đề phải từ 5 đến 255 ký tự")
    private String title;

    @NotBlank(message = "Nội dung không được để trống")
    @Size(min = 10, message = "Nội dung phải ít nhất 10 ký tự")
    private String content;

    // FIX: Truyền categoryId thay vì String category
    private Long categoryId;

    // Danh sách tag ids
    private List<Long> tagIds;
}