package com.Senior.Golves_Project.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_thresholds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserThreshold {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Integer thumbThreshold;
    private Integer indexThreshold;
    private Integer middleThreshold;
    private Integer ringThreshold;
    private Integer pinkyThreshold;
}
