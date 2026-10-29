package com.Senior.Golves_Project.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SensorDataRequest {
    @NotNull
    private Long userId;

    private Long modeId;

    @NotNull
    private Integer thumb;

    @NotNull
    private Integer index;

    @NotNull
    private Integer middle;

    @NotNull
    private Integer ring;

    @NotNull
    private Integer pinky;

    private Boolean sos = false;

    private Double accX;
    private Double accY;
    private Double accZ;
    private Double gyroX;
    private Double gyroY;
    private Double gyroZ;
    private String source;
}

