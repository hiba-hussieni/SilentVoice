package com.Senior.Golves_Project.repository;

import com.Senior.Golves_Project.entity.Mode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ModeRepository extends JpaRepository<Mode, Long> {
    List<Mode> findByUserIdOrderByCreatedAtAsc(Long userId);
    Optional<Mode> findByUserIdAndActiveTrue(Long userId);
    Optional<Mode> findByUserIdAndNameIgnoreCase(Long userId, String name);
    
    boolean existsByUserIdAndNameIgnoreCase(Long userId, String name);
}

