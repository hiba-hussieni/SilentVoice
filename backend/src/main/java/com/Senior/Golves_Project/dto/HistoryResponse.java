package com.Senior.Golves_Project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class HistoryResponse {
    private String gesture;
    private String meaning;
    private String sentence;
    private String mode;
    private String fingerPattern;
    private LocalDateTime createdAt;
}

