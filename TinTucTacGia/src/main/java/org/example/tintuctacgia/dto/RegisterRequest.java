package org.example.tintuctacgia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;

import java.time.LocalDate;

import org.example.tintuctacgia.enums.Role;

@Data
public class RegisterRequest {

    @NotBlank(
            message = "Tên không được để trống"
    )
    private String name;

    @Email(
            message = "Email không hợp lệ"
    )
    @NotBlank(
            message = "Email không được để trống"
    )
    private String email;

    @Size(
            min = 6,
            message = "Mật khẩu tối thiểu 6 ký tự"
    )
    private String password;

    @NotNull(
            message = "Ngày sinh không được để trống"
    )
    private LocalDate dateOfBirth;

    @NotNull(
            message = "Role không được để trống"
    )
    private Role role;
}