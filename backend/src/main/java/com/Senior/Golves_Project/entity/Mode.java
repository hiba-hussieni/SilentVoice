package com.Senior.Golves_Project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "modes")
public class Mode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = false;

    @Builder.Default
    @Column(name = "icon_name")
    private String iconName = "default";

    @Builder.Default
    @Column(name = "color_hex")
    private String colorHex = "#7B61FF";

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();

        if (active == null) {
            active = false;
        }

        if (iconName == null) {
            iconName = "default";
        }

        if (colorHex == null) {
            colorHex = "#7B61FF";
        }
    }
}