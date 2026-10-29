package com.Senior.Golves_Project.service;

import com.Senior.Golves_Project.dto.GestureResponse;
import com.Senior.Golves_Project.dto.ModeResponse;
import com.Senior.Golves_Project.dto.SetupResponse;
import com.Senior.Golves_Project.entity.FingerState;
import com.Senior.Golves_Project.entity.GestureMapping;
import com.Senior.Golves_Project.entity.Mode;
import com.Senior.Golves_Project.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultSetupService {
    private final UserService userService;
    private final ModeService modeService;
    private final GestureMatchingService gestureMatchingService;

    @Transactional
    public SetupResponse createDefaults(Long userId) {
        User user = userService.getUserOrThrow(userId);
        Mode home = modeService.findOrCreateMode(user, "Home", true, "home", "#4ECDC4");
        Mode emergency = modeService.findOrCreateMode(user, "Emergency", false, "emergency", "#FF6B6B");
        Mode health = modeService.findOrCreateMode(user, "Health", false, "health", "#B792F0");
        Mode work = modeService.findOrCreateMode(user, "Work", false, "work", "#7B61FF");
        Mode school = modeService.findOrCreateMode(user, "School", false, "education", "#F4D64E");
        List<GestureMapping> created = new ArrayList<>();

        addCommonHome(user, home, created);
        addEmergency(user, emergency, created);
        addHealth(user, health, created);
        addWork(user, work, created);
        addSchool(user, school, created);

        List<ModeResponse> modes = List.of(home, emergency, health, work, school)
                .stream()
                .map(modeService::toResponse)
                .toList();

        List<GestureResponse> gestures = created.stream()
                .map(gestureMatchingService::toResponse)
                .toList();

        return SetupResponse.builder()
                .userId(user.getId())
                .status("DEFAULT_SETUP_READY")
                .modes(modes)
                .gestures(gestures)
                .build();
    }

    private void addCommonHome(User user, Mode mode, List<GestureMapping> out) {
        out.add(add(user, mode, "HELLO", "hello", FingerState.OPEN, FingerState.OPEN, FingerState.OPEN, FingerState.OPEN, FingerState.OPEN));
        out.add(add(user, mode, "YES", "yes", FingerState.OPEN, FingerState.CLOSED, FingerState.CLOSED, FingerState.CLOSED, FingerState.CLOSED));
        out.add(add(user, mode, "NO", "no", FingerState.CLOSED, FingerState.OPEN, FingerState.OPEN, FingerState.CLOSED, FingerState.CLOSED));
        out.add(add(user, mode, "DRINK", "water", FingerState.CLOSED, FingerState.OPEN, FingerState.CLOSED, FingerState.CLOSED, FingerState.CLOSED));
        out.add(add(user, mode, "FOOD", "food", FingerState.CLOSED, FingerState.CLOSED, FingerState.OPEN, FingerState.CLOSED, FingerState.CLOSED));
        out.add(add(user, mode, "HELP", "help me", FingerState.OPEN, FingerState.CLOSED, FingerState.CLOSED, FingerState.CLOSED, FingerState.OPEN));
    }

    private void addEmergency(User user, Mode mode, List<GestureMapping> out) {
        out.add(add(user, mode, "HELP", "Call emergency!", FingerState.OPEN, FingerState.CLOSED, FingerState.CLOSED, FingerState.CLOSED, FingerState.OPEN));
        out.add(add(user, mode, "STOP", "Stop immediately", FingerState.OPEN, FingerState.OPEN, FingerState.OPEN, FingerState.OPEN, FingerState.OPEN));
        out.add(add(user, mode, "DANGER", "I am in danger", FingerState.CLOSED, FingerState.OPEN, FingerState.OPEN, FingerState.OPEN, FingerState.CLOSED));
        out.add(add(user, mode, "CALL", "Call someone for me", FingerState.OPEN, FingerState.OPEN, FingerState.CLOSED, FingerState.CLOSED, FingerState.CLOSED));
        out.add(add(user, mode, "SOS", "Emergency SOS", FingerState.CLOSED, FingerState.CLOSED, FingerState.CLOSED, FingerState.CLOSED, FingerState.CLOSED));
    }

    private void addHealth(User user, Mode mode, List<GestureMapping> out) {
        out.add(add(user, mode, "PAIN", "I feel pain", FingerState.CLOSED, FingerState.CLOSED, FingerState.OPEN, FingerState.OPEN, FingerState.CLOSED));
        out.add(add(user, mode, "MEDICINE", "I need medicine", FingerState.OPEN, FingerState.CLOSED, FingerState.OPEN, FingerState.CLOSED, FingerState.OPEN));
        out.add(add(user, mode, "DOCTOR", "I need a doctor", FingerState.CLOSED, FingerState.OPEN, FingerState.CLOSED, FingerState.OPEN, FingerState.CLOSED));
        out.add(add(user, mode, "TIRED", "I feel tired", FingerState.CLOSED, FingerState.CLOSED, FingerState.CLOSED, FingerState.OPEN, FingerState.OPEN));
        out.add(add(user, mode, "WATER", "I need water", FingerState.CLOSED, FingerState.OPEN, FingerState.CLOSED, FingerState.CLOSED, FingerState.CLOSED));
    }

    private void addWork(User user, Mode mode, List<GestureMapping> out) {
        out.add(add(user, mode, "MEETING", "I have a meeting", FingerState.OPEN, FingerState.CLOSED, FingerState.CLOSED, FingerState.OPEN, FingerState.OPEN));
        out.add(add(user, mode, "BREAK", "I need a break", FingerState.CLOSED, FingerState.OPEN, FingerState.OPEN, FingerState.CLOSED, FingerState.OPEN));
        out.add(add(user, mode, "DONE", "I finished the task", FingerState.OPEN, FingerState.OPEN, FingerState.CLOSED, FingerState.CLOSED, FingerState.OPEN));
        out.add(add(user, mode, "HELP", "I need help with work", FingerState.OPEN, FingerState.CLOSED, FingerState.CLOSED, FingerState.CLOSED, FingerState.OPEN));
        out.add(add(user, mode, "WAIT", "Please wait", FingerState.CLOSED, FingerState.CLOSED, FingerState.OPEN, FingerState.OPEN, FingerState.OPEN));
    }

    private void addSchool(User user, Mode mode, List<GestureMapping> out) {
        out.add(add(user, mode, "TEACHER", "I need the teacher", FingerState.OPEN, FingerState.OPEN, FingerState.CLOSED, FingerState.OPEN, FingerState.CLOSED));
        out.add(add(user, mode, "BOOK", "I need my book", FingerState.CLOSED, FingerState.OPEN, FingerState.CLOSED, FingerState.CLOSED, FingerState.OPEN));
        out.add(add(user, mode, "QUESTION", "I have a question", FingerState.CLOSED, FingerState.OPEN, FingerState.OPEN, FingerState.OPEN, FingerState.OPEN));
        out.add(add(user, mode, "HELP", "I need help in class", FingerState.OPEN, FingerState.CLOSED, FingerState.CLOSED, FingerState.CLOSED, FingerState.OPEN));
        out.add(add(user, mode, "BREAK", "I need a break", FingerState.CLOSED, FingerState.OPEN, FingerState.OPEN, FingerState.CLOSED, FingerState.OPEN));
    }

    private GestureMapping add(User user, Mode mode, String gestureName, String meaning,
                               FingerState thumb, FingerState index, FingerState middle,
                               FingerState ring, FingerState pinky) {
        return gestureMatchingService.createGestureIfPatternFree(user, mode, gestureName, meaning, thumb, index, middle, ring, pinky);
    }
}

