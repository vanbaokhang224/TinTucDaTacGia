package org.example.tintuctacgia.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.example.tintuctacgia.dto.profile.AuthorProfileRequest;
import org.example.tintuctacgia.dto.profile.EditorProfileRequest;
import org.example.tintuctacgia.dto.profile.ReaderProfileRequest;
import org.example.tintuctacgia.entity.User;
import org.example.tintuctacgia.exception.UnauthorizedException;
import org.example.tintuctacgia.service.ProfileService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // GET MY PROFILE - xem profile của chính mình
    // GET /api/profiles/me
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(
            @AuthenticationPrincipal User currentUser
    ) {
        return ResponseEntity.ok(profileService.getMyProfile(currentUser));
    }

    // GET PROFILE BY ID - ai cũng xem được
    // GET /api/profiles/1
    @GetMapping("/{id}")
    public ResponseEntity<?> getProfile(
            @PathVariable Long id
    ) {
        try {
            return ResponseEntity.ok(profileService.getProfile(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // UPDATE AUTHOR PROFILE
    // PUT /api/profiles/author/1
    @PutMapping("/author/{id}")
    public ResponseEntity<?> updateAuthorProfile(
            @PathVariable Long id,
            @Valid @RequestBody AuthorProfileRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        try {
            return ResponseEntity.ok(
                    profileService.updateAuthorProfile(id, request, currentUser)
            );
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // UPDATE EDITOR PROFILE
    // PUT /api/profiles/editor/1
    @PutMapping("/editor/{id}")
    public ResponseEntity<?> updateEditorProfile(
            @PathVariable Long id,
            @Valid @RequestBody EditorProfileRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        try {
            return ResponseEntity.ok(
                    profileService.updateEditorProfile(id, request, currentUser)
            );
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // UPDATE READER PROFILE
    // PUT /api/profiles/reader/1
    @PutMapping("/reader/{id}")
    public ResponseEntity<?> updateReaderProfile(
            @PathVariable Long id,
            @Valid @RequestBody ReaderProfileRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        try {
            return ResponseEntity.ok(
                    profileService.updateReaderProfile(id, request, currentUser)
            );
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
