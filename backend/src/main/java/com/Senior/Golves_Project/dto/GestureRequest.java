package com.Senior.Golves_Project.dto;

import com.Senior.Golves_Project.entity.FingerState;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GestureRequest {
    @NotNull
    private Long userId;

    @NotNull
    private Long modeId;

    @NotBlank
    private String gestureName;

    @NotBlank
    private String meaning;

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
}

