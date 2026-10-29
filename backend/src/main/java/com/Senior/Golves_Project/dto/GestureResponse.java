package com.Senior.Golves_Project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class GestureResponse {
    private Long id;
    private String gestureName;
    private String meaning;
    private String pattern;
}
