package com.Senior.Golves_Project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Builder.Default
    @Column(length = 20)
    private String phone = "";

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Builder.Default
    @Column(nullable = false)
    private Integer thumbThreshold = 1000;

    @Builder.Default
    @Column(nullable = false)
    private Integer indexThreshold = 1000;

    @Builder.Default
    @Column(nullable = false)
    private Integer middleThreshold = 1000;

    @Builder.Default
    @Column(nullable = false)
    private Integer ringThreshold = 1000;

    @Builder.Default
    @Column(nullable = false)
    private Integer pinkyThreshold = 1000;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mode> modes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}