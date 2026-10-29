package com.Senior.Golves_Project.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActiveModeRequest {
    @NotNull
    private Long userId;

    @NotNull
    private Long modeId;
}
