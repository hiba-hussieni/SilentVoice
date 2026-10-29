package com.Senior.Golves_Project.controller;

import com.Senior.Golves_Project.dto.ThresholdRequest;
import com.Senior.Golves_Project.dto.ThresholdResponse;
import com.Senior.Golves_Project.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/thresholds")
@RequiredArgsConstructor
public class ThresholdController {
    private final UserService userService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<ThresholdResponse> getThresholds(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getThresholds(userId));
    }

    @PutMapping
    public ResponseEntity<ThresholdResponse> updateThresholds(@Valid @RequestBody ThresholdRequest request) {
        return ResponseEntity.ok(userService.updateThresholds(request));
    }
}
