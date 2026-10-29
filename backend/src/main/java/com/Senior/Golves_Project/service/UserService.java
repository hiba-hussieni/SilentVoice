package com.Senior.Golves_Project.service;

import com.Senior.Golves_Project.dto.ThresholdRequest;
import com.Senior.Golves_Project.dto.ThresholdResponse;
import com.Senior.Golves_Project.dto.UpdateProfileRequest;
import com.Senior.Golves_Project.dto.UserRequest;
import com.Senior.Golves_Project.dto.UserResponse;
import com.Senior.Golves_Project.entity.FingerState;
import com.Senior.Golves_Project.entity.GestureMapping;
import com.Senior.Golves_Project.entity.Mode;
import com.Senior.Golves_Project.entity.User;
import com.Senior.Golves_Project.repository.GestureMappingRepository;
import com.Senior.Golves_Project.repository.ModeRepository;
import com.Senior.Golves_Project.repository.UserRepository;
import com.Senior.Golves_Project.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModeRepository modeRepository;
    private final GestureMappingRepository gestureMappingRepository;

    // ================= REGISTER =================

    public UserResponse register(UserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(PasswordUtil.hashPassword(request.getPassword()))
                .thumbThreshold(1000)
                .indexThreshold(1000)
                .middleThreshold(1000)
                .ringThreshold(1000)
                .pinkyThreshold(1000)
                .build();

        User saved = userRepository.save(user);

        createDefaultModesAndGestures(saved);

        return toResponse(saved);
    }

    // ================= DEFAULT SETUP =================

    private void createDefaultModesAndGestures(User user) {

        Mode defaultMode = Mode.builder()
                .name("Default")
                .active(true)
                .iconName("home")
                .colorHex("#4CAF50")
                .user(user)
                .build();

        Mode emergencyMode = Mode.builder()
                .name("Emergency")
                .active(false)
                .iconName("warning")
                .colorHex("#F44336")
                .user(user)
                .build();

        Mode savedDefaultMode = modeRepository.save(defaultMode);
        Mode savedEmergencyMode = modeRepository.save(emergencyMode);

        saveGesture(
                user,
                savedDefaultMode,
                "water",
                "water",
                FingerState.OPEN,
                FingerState.CLOSED,
                FingerState.OPEN,
                FingerState.OPEN,
                FingerState.CLOSED
        );

        saveGesture(
                user,
                savedEmergencyMode,
                "help",
                "help",
                FingerState.OPEN,
                FingerState.CLOSED,
                FingerState.CLOSED,
                FingerState.CLOSED,
                FingerState.CLOSED
        );
    }

    private void saveGesture(
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

        gestureMappingRepository.save(gesture);
    }

    // ================= UPDATE PROFILE =================

    public UserResponse updateProfile(UpdateProfileRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());

        User updatedUser = userRepository.save(user);

        return toResponse(updatedUser);
    }

    // ================= DELETE USER =================

    @Transactional
    public void deleteUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        gestureMappingRepository.deleteByUserId(userId);

        userRepository.delete(user);
    }

    // ================= USERS =================

    public User getUserOrThrow(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found with id: " + id));
    }

    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ================= THRESHOLDS =================

    public ThresholdResponse getThresholds(Long userId) {

        return toThresholdResponse(getUserOrThrow(userId));
    }

    public ThresholdResponse updateThresholds(ThresholdRequest request) {

        validateThreshold(request.getThumbThreshold(), "thumbThreshold");
        validateThreshold(request.getIndexThreshold(), "indexThreshold");
        validateThreshold(request.getMiddleThreshold(), "middleThreshold");
        validateThreshold(request.getRingThreshold(), "ringThreshold");
        validateThreshold(request.getPinkyThreshold(), "pinkyThreshold");

        User user = getUserOrThrow(request.getUserId());

        user.setThumbThreshold(request.getThumbThreshold());
        user.setIndexThreshold(request.getIndexThreshold());
        user.setMiddleThreshold(request.getMiddleThreshold());
        user.setRingThreshold(request.getRingThreshold());
        user.setPinkyThreshold(request.getPinkyThreshold());

        return toThresholdResponse(userRepository.save(user));
    }

    private void validateThreshold(Integer value, String fieldName) {

        if (value == null || value < 0) {
            throw new RuntimeException(
                    fieldName + " must be a positive number"
            );
        }
    }

    // ================= RESPONSES =================

    public UserResponse toResponse(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }

    private ThresholdResponse toThresholdResponse(User user) {

        return ThresholdResponse.builder()
                .userId(user.getId())
                .thumbThreshold(user.getThumbThreshold())
                .indexThreshold(user.getIndexThreshold())
                .middleThreshold(user.getMiddleThreshold())
                .ringThreshold(user.getRingThreshold())
                .pinkyThreshold(user.getPinkyThreshold())
                .build();
    }
}