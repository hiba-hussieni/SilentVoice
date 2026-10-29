package com.Senior.Golves_Project.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackRequest {

    private Long userId;

    private String mood;
}