package org.example.tintuctacgia.service;

import lombok.RequiredArgsConstructor;

import org.example.tintuctacgia.dto.profile.AuthorProfileRequest;
import org.example.tintuctacgia.dto.profile.AuthorProfileResponse;
import org.example.tintuctacgia.dto.profile.EditorProfileRequest;
import org.example.tintuctacgia.dto.profile.EditorProfileResponse;
import org.example.tintuctacgia.dto.profile.ReaderProfileRequest;
import org.example.tintuctacgia.dto.profile.ReaderProfileResponse;
import org.example.tintuctacgia.dto.auth.UserResponse;
import org.example.tintuctacgia.entity.*;
import org.example.tintuctacgia.enums.Role;
import org.example.tintuctacgia.exception.UnauthorizedException;
import org.example.tintuctacgia.exception.UserNotFoundException;
import org.example.tintuctacgia.repository.UserRepository;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;

    // GET PROFILE - trả về đúng type theo role
    public Object getProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return buildProfileResponse(user);
    }

    // GET MY PROFILE
    public Object getMyProfile(User currentUser) {
        // Reload từ DB để lấy đầy đủ thông tin subclass
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new UserNotFoundException(currentUser.getId()));
        return buildProfileResponse(user);
    }

    // UPDATE AUTHOR PROFILE
    public AuthorProfileResponse updateAuthorProfile(
            Long id,
            AuthorProfileRequest request,
            User currentUser
    ) {
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isSelf = currentUser.getId().equals(id);

        if (!isAdmin && !isSelf) {
            throw new UnauthorizedException("Bạn không có quyền cập nhật profile này");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!(user instanceof Author)) {
            throw new UnauthorizedException("User này không phải AUTHOR");
        }

        Author author = (Author) user;
        if (request.getBio() != null) author.setBio(request.getBio());
        if (request.getSpecialty() != null) author.setSpecialty(request.getSpecialty());

        userRepository.save(author);
        return buildAuthorResponse(author);
    }

    // UPDATE EDITOR PROFILE
    public EditorProfileResponse updateEditorProfile(
            Long id,
            EditorProfileRequest request,
            User currentUser
    ) {
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isSelf = currentUser.getId().equals(id);

        if (!isAdmin && !isSelf) {
            throw new UnauthorizedException("Bạn không có quyền cập nhật profile này");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!(user instanceof Editor)) {
            throw new UnauthorizedException("User này không phải EDITOR");
        }

        Editor editor = (Editor) user;
        if (request.getBio() != null) editor.setBio(request.getBio());
        if (request.getDepartment() != null) editor.setDepartment(request.getDepartment());

        userRepository.save(editor);
        return buildEditorResponse(editor);
    }

    // UPDATE READER PROFILE
    public ReaderProfileResponse updateReaderProfile(
            Long id,
            ReaderProfileRequest request,
            User currentUser
    ) {
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isSelf = currentUser.getId().equals(id);

        if (!isAdmin && !isSelf) {
            throw new UnauthorizedException("Bạn không có quyền cập nhật profile này");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!(user instanceof Reader)) {
            throw new UnauthorizedException("User này không phải READER");
        }

        Reader reader = (Reader) user;
        if (request.getBio() != null) reader.setBio(request.getBio());

        userRepository.save(reader);
        return buildReaderResponse(reader);
    }

    // Helper - build đúng response theo role
    private Object buildProfileResponse(User user) {
        if (user instanceof Author author) {
            return buildAuthorResponse(author);
        } else if (user instanceof Editor editor) {
            return buildEditorResponse(editor);
        } else if (user instanceof Reader reader) {
            return buildReaderResponse(reader);
        } else {
            // Admin - dùng UserResponse thông thường
            return UserResponse.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .dateOfBirth(user.getDateOfBirth())
                    .role(user.getRole())
                    .build();
        }
    }

    private AuthorProfileResponse buildAuthorResponse(Author author) {
        return AuthorProfileResponse.builder()
                .id(author.getId())
                .name(author.getName())
                .email(author.getEmail())
                .dateOfBirth(author.getDateOfBirth())
                .role(author.getRole())
                .bio(author.getBio())
                .specialty(author.getSpecialty())
                .totalPosts(author.getTotalPosts())
                .build();
    }

    private EditorProfileResponse buildEditorResponse(Editor editor) {
        return EditorProfileResponse.builder()
                .id(editor.getId())
                .name(editor.getName())
                .email(editor.getEmail())
                .dateOfBirth(editor.getDateOfBirth())
                .role(editor.getRole())
                .bio(editor.getBio())
                .department(editor.getDepartment())
                .totalApproved(editor.getTotalApproved())
                .build();
    }

    private ReaderProfileResponse buildReaderResponse(Reader reader) {
        return ReaderProfileResponse.builder()
                .id(reader.getId())
                .name(reader.getName())
                .email(reader.getEmail())
                .dateOfBirth(reader.getDateOfBirth())
                .role(reader.getRole())
                .bio(reader.getBio())
                .build();
    }
}
