package com.Senior.Golves_Project.controller;

import com.Senior.Golves_Project.dto.SetupResponse;
import com.Senior.Golves_Project.service.DefaultSetupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/setup")
@RequiredArgsConstructor
public class SetupController {
    private final DefaultSetupService defaultSetupService;

    @PostMapping("/defaults/{userId}")
    public ResponseEntity<SetupResponse> createDefaults(@PathVariable Long userId) {
        return ResponseEntity.ok(defaultSetupService.createDefaults(userId));
    }
}
