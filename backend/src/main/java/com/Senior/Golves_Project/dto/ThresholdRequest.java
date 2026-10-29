package com.Senior.Golves_Project.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThresholdRequest {
    @NotNull
    private Long userId;

    @NotNull
    private Integer thumbThreshold;

    @NotNull
    private Integer indexThreshold;

    @NotNull
    private Integer middleThreshold;

    @NotNull
    private Integer ringThreshold;

    @NotNull
    private Integer pinkyThreshold;
}

