package com.Senior.Golves_Project.dto;

import com.Senior.Golves_Project.entity.FingerState;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddModeWithGestureRequest {

    @NotNull
    private Long userId;

    @NotBlank
    private String modeName;

    @NotBlank
    private String word;

    // 🔥 NEW
    private String iconName;

    // 🔥 NEW
    private String colorHex;

    @NotNull
    private FingerState thumbState;

    @NotNull
    private FingerState indexState;

    @NotNull
    private FingerState middleState;

    @NotNull
    private FingerState ringState;

    @NotNull
    private FingerState pinkyState;

    private int accX;
    private int accY;
    private int accZ;

    private int gyroX;
    private int gyroY;
    private int gyroZ;
}