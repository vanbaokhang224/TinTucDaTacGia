package org.example.tintuctacgia.dto.profile;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthorProfileRequest {

    @Size(max = 500, message = "Bio tối đa 500 ký tự")
    private String bio;

    private String specialty;
}
