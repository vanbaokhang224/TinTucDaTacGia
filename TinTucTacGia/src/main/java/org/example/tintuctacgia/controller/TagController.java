package org.example.tintuctacgia.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tintuctacgia.dto.tag.TagRequest;
import org.example.tintuctacgia.entity.User;
import org.example.tintuctacgia.enums.Role;
import org.example.tintuctacgia.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    // GET ALL - ai cũng xem được
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(tagService.getAll());
    }

    // CREATE - ADMIN hoặc EDITOR
    @PostMapping
    public ResponseEntity<?> create(
            @Valid @RequestBody TagRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser.getRole() != Role.ADMIN && currentUser.getRole() != Role.EDITOR) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Chỉ ADMIN hoặc EDITOR mới được tạo tag"));
        }
        return ResponseEntity.ok(tagService.create(request));
    }

    // DELETE - chỉ ADMIN
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Chỉ ADMIN mới được xóa tag"));
        }
        tagService.delete(id);
        return ResponseEntity.ok(Map.of("message", "Xóa tag thành công"));
    }
}