package com.Senior.Golves_Project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ThresholdResponse {
    private Long userId;
    private Integer thumbThreshold;
    private Integer indexThreshold;
    private Integer middleThreshold;
    private Integer ringThreshold;
    private Integer pinkyThreshold;
}

