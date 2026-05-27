package org.example.tintuctacgia.dto.tag;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TagRequest {

    @NotBlank(message = "Tên tag không được để trống")
    private String name;
}
