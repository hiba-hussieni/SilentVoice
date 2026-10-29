package com.Senior.Golves_Project.controller;

import com.Senior.Golves_Project.dto.ActiveModeRequest;
import com.Senior.Golves_Project.dto.ModeRequest;
import com.Senior.Golves_Project.dto.ModeResponse;
import com.Senior.Golves_Project.service.ModeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modes")
@RequiredArgsConstructor
public class ModeController {
    private final ModeService modeService;

    @PostMapping
    public ResponseEntity<ModeResponse> createMode(@Valid @RequestBody ModeRequest request) {
        return ResponseEntity.ok(modeService.createMode(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ModeResponse>> getUserModes(@PathVariable Long userId) {
        return ResponseEntity.ok(modeService.getUserModes(userId));
    }

    @PutMapping("/active")
    public ResponseEntity<ModeResponse> setActiveMode(@Valid @RequestBody ActiveModeRequest request) {
        return ResponseEntity.ok(modeService.setActiveMode(request));
    }
    @DeleteMapping("/user/{userId}/name/{modeName}")
    public ResponseEntity<String> deleteMode(
            @PathVariable Long userId,
            @PathVariable String modeName
    ) {
        modeService.deleteModeByName(userId, modeName);
        return ResponseEntity.ok("Mode deleted successfully");
    }
}

