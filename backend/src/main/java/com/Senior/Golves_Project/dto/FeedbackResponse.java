package com.Senior.Golves_Project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class FeedbackResponse {

    private String status;

    private String message;
}