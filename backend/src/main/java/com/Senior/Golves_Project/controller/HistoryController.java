package com.Senior.Golves_Project.controller;

import com.Senior.Golves_Project.dto.HistoryResponse;
import com.Senior.Golves_Project.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {
    private final HistoryService historyService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<HistoryResponse>> getUserHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(historyService.getUserHistory(userId));
    }
}

