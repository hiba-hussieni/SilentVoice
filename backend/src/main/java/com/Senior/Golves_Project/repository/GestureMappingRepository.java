package com.Senior.Golves_Project.repository;

import com.Senior.Golves_Project.entity.FingerState;
import com.Senior.Golves_Project.entity.GestureMapping;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GestureMappingRepository
        extends JpaRepository<GestureMapping, Long> {

    List<GestureMapping> findByUserIdAndModeIdOrderByCreatedAtAsc(
            Long userId,
            Long modeId
    );

    boolean existsByUserIdAndModeIdAndThumbStateAndIndexStateAndMiddleStateAndRingStateAndPinkyState(
            Long userId,
            Long modeId,
            FingerState thumbState,
            FingerState indexState,
            FingerState middleState,
            FingerState ringState,
            FingerState pinkyState
    );

    Optional<GestureMapping>
    findFirstByUserIdAndModeIdAndThumbStateAndIndexStateAndMiddleStateAndRingStateAndPinkyState(
            Long userId,
            Long modeId,
            FingerState thumbState,
            FingerState indexState,
            FingerState middleState,
            FingerState ringState,
            FingerState pinkyState
    );

    List<GestureMapping>
    findByUserIdAndModeIdAndThumbStateAndIndexStateAndMiddleStateAndRingStateAndPinkyState(
            Long userId,
            Long modeId,
            FingerState thumbState,
            FingerState indexState,
            FingerState middleState,
            FingerState ringState,
            FingerState pinkyState
    );

    void deleteByUserIdAndModeId(
            Long userId,
            Long modeId
    );

    void deleteByUserId(Long userId);
}