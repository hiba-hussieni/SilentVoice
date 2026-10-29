package com.Senior.Golves_Project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class SetupResponse {
    private Long userId;
    private String status;
    private List<ModeResponse> modes;
    private List<GestureResponse> gestures;
}

