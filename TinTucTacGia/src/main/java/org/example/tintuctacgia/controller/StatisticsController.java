package org.example.tintuctacgia.controller;

import lombok.RequiredArgsConstructor;

import org.example.tintuctacgia.entity.User;
import org.example.tintuctacgia.enums.Role;
import org.example.tintuctacgia.service.StatisticsService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    // THỐNG KÊ TỔNG QUAN - chỉ ADMIN
    // GET /api/statistics/system
    @GetMapping("/system")
    public ResponseEntity<?> getSystemStatistics(
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Chỉ ADMIN mới xem được thống kê hệ thống"));
        }
        return ResponseEntity.ok(statisticsService.getSystemStatistics());
    }

    // THỐNG KÊ CỦA CHÍNH MÌNH - AUTHOR tự xem
    // GET /api/statistics/me
    @GetMapping("/me")
    public ResponseEntity<?> getMyStatistics(
            @AuthenticationPrincipal User currentUser
    ) {
        if (currentUser.getRole() != Role.AUTHOR && currentUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Chỉ AUTHOR mới xem được thống kê của mình"));
        }
        return ResponseEntity.ok(statisticsService.getMyStatistics(currentUser));
    }

    // THỐNG KÊ CỦA 1 TÁC GIẢ - ADMIN xem
    // GET /api/statistics/author/1
    @GetMapping("/author/{authorId}")
    public ResponseEntity<?> getAuthorStatistics(
            @PathVariable Long authorId,
            @AuthenticationPrincipal User currentUser
    ) {
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isSelf = currentUser.getId().equals(authorId);

        if (!isAdmin && !isSelf) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Bạn không có quyền xem thống kê này"));
        }
        return ResponseEntity.ok(statisticsService.getAuthorStatistics(authorId));
    }
}
