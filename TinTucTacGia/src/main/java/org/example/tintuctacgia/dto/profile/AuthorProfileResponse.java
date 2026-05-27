package org.example.tintuctacgia.dto.profile;

import lombok.*;
import org.example.tintuctacgia.enums.Role;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorProfileResponse {
    private Long id;
    private String name;
    private String email;
    private LocalDate dateOfBirth;
    private Role role;
    private String bio;
    private String specialty;
    private int totalPosts;
}
