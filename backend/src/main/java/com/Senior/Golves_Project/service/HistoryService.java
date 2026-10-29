package com.Senior.Golves_Project.service;

import com.Senior.Golves_Project.dto.HistoryResponse;
import com.Senior.Golves_Project.entity.Mode;
import com.Senior.Golves_Project.entity.TranslationHistory;
import com.Senior.Golves_Project.entity.User;
import com.Senior.Golves_Project.repository.TranslationHistoryRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoryService {
    private final TranslationHistoryRepository translationHistoryRepository;

    public TranslationHistory save(User user, Mode mode, String pattern, String gesture, String meaning, String sentence, Boolean sos) {
        TranslationHistory history = TranslationHistory.builder()
                .user(user)
                .mode(mode)
                .fingerPattern(pattern)
                .gestureName(gesture)
                .meaning(meaning)
                .sentence(sentence)
                .sos(sos != null && sos)
                .build();
        return translationHistoryRepository.save(history);
    }

    public List<HistoryResponse> getUserHistory(Long userId) {
        return translationHistoryRepository.findTop50ByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private HistoryResponse toResponse(TranslationHistory history) {
        return HistoryResponse.builder()
                .gesture(history.getGestureName())
                .meaning(history.getMeaning())
                .sentence(history.getSentence())
                .mode(history.getMode() == null ? null : history.getMode().getName())
                .fingerPattern(history.getFingerPattern())
                .createdAt(history.getCreatedAt())
                .build();
    }
}
