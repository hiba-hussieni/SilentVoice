package com.Senior.Golves_Project.service;

import com.Senior.Golves_Project.dto.SensorDataRequest;
import com.Senior.Golves_Project.dto.TranslationResponse;
import com.Senior.Golves_Project.entity.GestureMapping;
import com.Senior.Golves_Project.entity.Mode;
import com.Senior.Golves_Project.entity.TranslationHistory;
import com.Senior.Golves_Project.entity.User;
import com.Senior.Golves_Project.repository.TranslationHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TranslationService {

    private final UserService userService;
    private final ModeService modeService;
    private final SensorProcessingService sensorProcessingService;
    private final GestureMatchingService gestureMatchingService;
    private final HistoryService historyService;
    private final OllamaService ollamaService;
    private final TranslationHistoryRepository historyRepository;

    public TranslationResponse translate(SensorDataRequest request) {

        User user;

        try {
            user = userService.getUserOrThrow(request.getUserId());
        } catch (Exception e) {
            user = userService.getUserOrThrow(1L);
        }

        Mode mode = request.getModeId() != null
                ? modeService.getModeOrThrow(request.getModeId())
                : modeService.getActiveModeOrThrow(user.getId());

        if (!mode.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Mode does not belong to this user");
        }

        // PROCESS SENSOR DATA USING CURRENT USER
        SensorProcessingService.FingerPattern pattern =
                sensorProcessingService.process(request, user);


        System.out.println("===== TRANSLATE DEBUG =====");
        System.out.println("userId=" + user.getId());
        System.out.println("modeId=" + mode.getId());
        System.out.println("modeName=" + mode.getName());
        System.out.println("thumbRaw=" + request.getThumb());
        System.out.println("indexRaw=" + request.getIndex());
        System.out.println("middleRaw=" + request.getMiddle());
        System.out.println("ringRaw=" + request.getRing());
        System.out.println("pinkyRaw=" + request.getPinky());
        System.out.println("pattern=" + pattern.asText());


        // MATCH ONLY INSIDE CURRENT MODE
        Optional<GestureMapping> matched =
                gestureMatchingService.matchGesture(
                        user.getId(),
                        mode.getId(),
                        pattern,
                        request.getAccX(),
                        request.getAccY(),
                        request.getAccZ(),
                        request.getGyroX(),
                        request.getGyroY(),
                        request.getGyroZ()
                );

        System.out.println("matched=" + matched.isPresent());

        matched.ifPresent(g -> {
            System.out.println("matchedGesture=" + g.getGestureName());
            System.out.println("matchedMeaning=" + g.getMeaning());
        });
        

        System.out.println("===========================");

        boolean sos = Boolean.TRUE.equals(request.getSos());

        String gestureName = matched
                .map(GestureMapping::getGestureName)
                .orElse("UNKNOWN_GESTURE");

        String meaning = matched
                .map(GestureMapping::getMeaning)
                .orElse("Unknown gesture");

        // EMERGENCY SOS
        if (sos && mode.getName().equals("Emergency")) {
            gestureName = "SOS";
            meaning = "Emergency SOS request";
        }

        boolean recognized = matched.isPresent() || sos;

        String sentence = meaning;
        Optional<TranslationHistory> last =
                historyRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId());

        boolean shouldSave = last.isEmpty()
                || !last.get().getGestureName().equals(gestureName)
                || !last.get().getFingerPattern().equals(pattern.asText());

        if (shouldSave) {

            historyService.save(
                    user,
                    mode,
                    pattern.asText(),
                    gestureName,
                    meaning,
                    sentence,
                    request.getSos()
            );
        }

        return TranslationResponse.builder()
                .status(recognized ? "SUCCESS" : "UNKNOWN")
                .fingerPattern(pattern.asText())
                .gesture(gestureName)
                .meaning(meaning)
                .sentence(sentence)
                .confidence(recognized ? "RULE_MATCH" : "NO_MATCH")
                .mode(mode.getName())
                .isRecognized(recognized)
                .build();
    }
}