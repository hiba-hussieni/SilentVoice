package com.Senior.Golves_Project.dto;

import lombok.*;

@Data
@Builder
public class TranslationResponse {

    private String status;
    private String fingerPattern;
    private String gesture;
    private String meaning;
    private String sentence;
    private String confidence;
    private String mode;

    private boolean isRecognized; // ✅ NEW
}
