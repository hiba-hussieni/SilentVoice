package com.Senior.Golves_Project.controller;

import com.Senior.Golves_Project.dto.FeedbackRequest;
import com.Senior.Golves_Project.dto.FeedbackResponse;
import com.Senior.Golves_Project.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("/submit")
    public ResponseEntity<?> submitFeedback(@RequestBody FeedbackRequest request) {
        try {
            FeedbackResponse response =
                    feedbackService.submitFeedback(request);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put(
                    "message",
                    e.getMessage() != null ? e.getMessage() : "Feedback failed"
            );

            return ResponseEntity.badRequest().body(error);
        }
    }
}