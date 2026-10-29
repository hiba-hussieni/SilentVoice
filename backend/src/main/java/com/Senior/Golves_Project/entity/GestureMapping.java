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
@Table(name = "gesture_mappings")
public class GestureMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mode_id", nullable = false)
    private Mode mode;

    @Column(nullable = false)
    private String gestureName;

    @Column(nullable = false)
    private String meaning;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FingerState thumbState;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FingerState indexState;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FingerState middleState;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FingerState ringState;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FingerState pinkyState;
    @Column(nullable = false)
    private int accX;

    @Column(nullable = false)
    private int accY;

    @Column(nullable = false)
    private int accZ;

    @Column(nullable = false)
    private int gyroX;

    @Column(nullable = false)
    private int gyroY;

    @Column(nullable = false)
    private int gyroZ;
    @Column(nullable = false)
    private LocalDateTime createdAt;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public String getPattern() {
        return thumbState + "," + indexState + "," + middleState + "," + ringState + "," + pinkyState;
    }
}

