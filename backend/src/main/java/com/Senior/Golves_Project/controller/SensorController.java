package com.Senior.Golves_Project.controller;

import com.Senior.Golves_Project.dto.SensorDataRequest;
import com.Senior.Golves_Project.dto.TranslationResponse;
import com.Senior.Golves_Project.service.TranslationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
public class SensorController {
    private final TranslationService translationService;

    @PostMapping("/translate")
    public ResponseEntity<TranslationResponse> translate(@Valid @RequestBody SensorDataRequest request) {
        return ResponseEntity.ok(translationService.translate(request));
    }
}