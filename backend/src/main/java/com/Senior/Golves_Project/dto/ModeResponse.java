package com.Senior.Golves_Project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ModeResponse {

    private Long id;
    private String name;
    private Boolean active;

    private String iconName;
    private String colorHex;
}