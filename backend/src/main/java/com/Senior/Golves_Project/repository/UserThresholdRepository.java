package com.Senior.Golves_Project.repository;

import com.Senior.Golves_Project.entity.UserThreshold;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserThresholdRepository extends JpaRepository<UserThreshold, Long> {
    Optional<UserThreshold> findByUserId(Long userId);
}
