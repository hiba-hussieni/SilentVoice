package com.Senior.Golves_Project.controller;

import com.Senior.Golves_Project.dto.AddModeWithGestureRequest;
import com.Senior.Golves_Project.dto.GestureRequest;
import com.Senior.Golves_Project.dto.GestureResponse;
import com.Senior.Golves_Project.service.GestureMatchingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gestures")
@RequiredArgsConstructor
public class GestureController {

    private final GestureMatchingService gestureMatchingService;

    @PostMapping
    public ResponseEntity<GestureResponse> createGesture(
            @Valid @RequestBody GestureRequest request
    ) {
        return ResponseEntity.ok(
                gestureMatchingService.createGesture(request)
        );
    }

    @PostMapping("/add-mode-with-gesture")
    public ResponseEntity<GestureResponse> addModeWithGesture(
            @Valid @RequestBody AddModeWithGestureRequest request
    ) {
        return ResponseEntity.ok(
                gestureMatchingService.addModeWithGesture(request)
        );
    }

    @GetMapping("/user/{userId}/mode/{modeId}")
    public ResponseEntity<List<GestureResponse>> getGestures(
            @PathVariable Long userId,
            @PathVariable Long modeId
    ) {
        return ResponseEntity.ok(
                gestureMatchingService.getGesturesByMode(userId, modeId)
        );
    }
}