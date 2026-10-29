package com.Senior.Golves_Project.service;

import com.Senior.Golves_Project.dto.FeedbackRequest;
import com.Senior.Golves_Project.dto.FeedbackResponse;
import com.Senior.Golves_Project.entity.Feedback;
import com.Senior.Golves_Project.entity.User;
import com.Senior.Golves_Project.repository.FeedbackRepository;
import com.Senior.Golves_Project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    public FeedbackResponse submitFeedback(FeedbackRequest request) {

        if (request.getUserId() == null || request.getMood() == null || request.getMood().trim().isEmpty()) {
            throw new RuntimeException("Missing feedback data");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Feedback feedback = Feedback.builder()
                .userId(user.getId())
                .mood(request.getMood())
                .build();

        feedbackRepository.save(feedback);

        return FeedbackResponse.builder()
                .status("SUCCESS")
                .message("Feedback submitted successfully")
                .build();
    }
}