package com.Senior.Golves_Project.service;

import com.Senior.Golves_Project.dto.AddModeWithGestureRequest;
import com.Senior.Golves_Project.dto.GestureRequest;
import com.Senior.Golves_Project.dto.GestureResponse;
import com.Senior.Golves_Project.entity.FingerState;
import com.Senior.Golves_Project.entity.GestureMapping;
import com.Senior.Golves_Project.entity.Mode;
import com.Senior.Golves_Project.entity.User;
import com.Senior.Golves_Project.repository.GestureMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GestureMatchingService {

    private final GestureMappingRepository gestureMappingRepository;
    private final UserService userService;
    private final ModeService modeService;

    public GestureResponse createGesture(GestureRequest request) {
        User user = userService.getUserOrThrow(request.getUserId());
        Mode mode = modeService.getModeOrThrow(request.getModeId());

        if (!mode.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Mode does not belong to this user");
        }

        boolean patternAlreadyUsed =
                gestureMappingRepository
                        .existsByUserIdAndModeIdAndThumbStateAndIndexStateAndMiddleStateAndRingStateAndPinkyState(
                                user.getId(),
                                mode.getId(),
                                request.getThumbState(),
                                request.getIndexState(),
                                request.getMiddleState(),
                                request.getRingState(),
                                request.getPinkyState()
                        );

        if (patternAlreadyUsed) {
            throw new RuntimeException("This finger pattern is already used in this mode");
        }

        GestureMapping gesture = GestureMapping.builder()
                .user(user)
                .mode(mode)
                .gestureName(request.getGestureName())
                .meaning(request.getMeaning())
                .thumbState(request.getThumbState())
                .indexState(request.getIndexState())
                .middleState(request.getMiddleState())
                .ringState(request.getRingState())
                .pinkyState(request.getPinkyState())
                .build();

        return toResponse(gestureMappingRepository.save(gesture));
    }

    public GestureMapping createGestureIfPatternFree(
            User user,
            Mode mode,
            String gestureName,
            String meaning,
            FingerState thumb,
            FingerState index,
            FingerState middle,
            FingerState ring,
            FingerState pinky
    ) {
        boolean exists =
                gestureMappingRepository
                        .existsByUserIdAndModeIdAndThumbStateAndIndexStateAndMiddleStateAndRingStateAndPinkyState(
                                user.getId(),
                                mode.getId(),
                                thumb,
                                index,
                                middle,
                                ring,
                                pinky
                        );

        if (exists) {
            return gestureMappingRepository
                    .findFirstByUserIdAndModeIdAndThumbStateAndIndexStateAndMiddleStateAndRingStateAndPinkyState(
                            user.getId(),
                            mode.getId(),
                            thumb,
                            index,
                            middle,
                            ring,
                            pinky
                    )
                    .orElseThrow();
        }

        GestureMapping gesture = GestureMapping.builder()
                .user(user)
                .mode(mode)
                .gestureName(gestureName)
                .meaning(meaning)
                .thumbState(thumb)
                .indexState(index)
                .middleState(middle)
                .ringState(ring)
                .pinkyState(pinky)
                .build();

        return gestureMappingRepository.save(gesture);
    }

    public List<GestureResponse> getGesturesByMode(Long userId, Long modeId) {
        return gestureMappingRepository
                .findByUserIdAndModeIdOrderByCreatedAtAsc(userId, modeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<GestureMapping> matchGesture(
            Long userId,
            Long modeId,
            SensorProcessingService.FingerPattern pattern,
            Double accX,
            Double accY,
            Double accZ,
            Double gyroX,
            Double gyroY,
            Double gyroZ
    ) {
        System.out.println("===== MATCH DEBUG =====");
        System.out.println("userId = " + userId);
        System.out.println("modeId = " + modeId);
        System.out.println("thumb = " + pattern.getThumb());
        System.out.println("index = " + pattern.getIndex());
        System.out.println("middle = " + pattern.getMiddle());
        System.out.println("ring = " + pattern.getRing());
        System.out.println("pinky = " + pattern.getPinky());

        List<GestureMapping> candidates =
                gestureMappingRepository
                        .findByUserIdAndModeIdAndThumbStateAndIndexStateAndMiddleStateAndRingStateAndPinkyState(
                                userId,
                                modeId,
                                pattern.getThumb(),
                                pattern.getIndex(),
                                pattern.getMiddle(),
                                pattern.getRing(),
                                pattern.getPinky()
                        );

        System.out.println("candidateCount = " + candidates.size());

        if (candidates.isEmpty()) {

            System.out.println("NO MATCH FOUND");
            System.out.println("=======================");

            return Optional.empty();
        }

        GestureMapping matched = candidates.get(0);

        double bestScore = Double.MAX_VALUE;

        for (GestureMapping candidate : candidates) {

            double score = 0;

            double normalizedAccX = normalizeAcc(accX);
            double normalizedAccY = normalizeAcc(accY);
            double normalizedAccZ = normalizeAcc(accZ);

            double normalizedGyroX = normalizeGyro(gyroX);
            double normalizedGyroY = normalizeGyro(gyroY);
            double normalizedGyroZ = normalizeGyro(gyroZ);

            score += Math.abs(candidate.getAccX() - normalizedAccX);
            score += Math.abs(candidate.getAccY() - normalizedAccY);
            score += Math.abs(candidate.getAccZ() - normalizedAccZ);

            score += Math.abs(candidate.getGyroX() - normalizedGyroX);
            score += Math.abs(candidate.getGyroY() - normalizedGyroY);
            score += Math.abs(candidate.getGyroZ() - normalizedGyroZ);
            System.out.println(
                    "Candidate = " + candidate.getGestureName()
                            + " score = " + score
            );

            if (score < bestScore) {
                bestScore = score;
                matched = candidate;
            }
        }

        System.out.println("BEST SCORE = " + bestScore);

        System.out.println("matchedGesture = " + matched.getGestureName());
        System.out.println("matchedMeaning = " + matched.getMeaning());

        System.out.println("=======================");

        return Optional.of(matched);
    }

    public GestureResponse toResponse(GestureMapping gesture) {
        return GestureResponse.builder()
                .id(gesture.getId())
                .gestureName(gesture.getGestureName())
                .meaning(gesture.getMeaning())
                .pattern(gesture.getPattern())
                .build();
    }

    public GestureResponse addModeWithGesture(AddModeWithGestureRequest request) {
        System.out.println("========== ADD MODE DEBUG ==========");
        System.out.println("MODE NAME = " + request.getModeName());
        System.out.println("WORD = " + request.getWord());

        System.out.println("THUMB = " + request.getThumbState());
        System.out.println("INDEX = " + request.getIndexState());
        System.out.println("MIDDLE = " + request.getMiddleState());
        System.out.println("RING = " + request.getRingState());
        System.out.println("PINKY = " + request.getPinkyState());

        User user = userService.getUserOrThrow(request.getUserId());

        Mode savedMode =
                modeService.findOrCreateMode(
                        user,
                        request.getModeName(),
                        true,
                        request.getIconName(),
                        request.getColorHex()
                );

        GestureMapping gesture = GestureMapping.builder()
                .user(user)
                .mode(savedMode)
                .gestureName(request.getWord())
                .meaning(request.getWord())
                .thumbState(request.getThumbState())
                .indexState(request.getIndexState())
                .middleState(request.getMiddleState())
                .ringState(request.getRingState())
                .pinkyState(request.getPinkyState())
                .accX(request.getAccX())
                .accY(request.getAccY())
                .accZ(request.getAccZ())
                .gyroX(request.getGyroX())
                .gyroY(request.getGyroY())
                .gyroZ(request.getGyroZ())
                .build();


        GestureMapping savedGesture =
                gestureMappingRepository.save(gesture);

        System.out.println("SAVED GESTURE ID = " + savedGesture.getId());
        System.out.println("SAVED WORD = " + savedGesture.getGestureName());
        System.out.println("===================================");

        return toResponse(savedGesture);
    }
    private double safe(Double value) {
        return value == null ? 0.0 : value;
    }

    private double normalizeAcc(Double value) {

        if (value == null) {
            return 0;
        }

        return value;
    }

    private double normalizeGyro(Double value) {

        if (value == null) {
            return 0;
        }

        return value;
    }
    
}