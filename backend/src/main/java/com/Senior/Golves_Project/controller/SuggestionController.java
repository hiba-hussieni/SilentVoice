package com.Senior.Golves_Project.controller;

import com.Senior.Golves_Project.dto.SuggestionRequest;
import com.Senior.Golves_Project.dto.SuggestionResponse;
import com.Senior.Golves_Project.service.OllamaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/suggestions")
@CrossOrigin("*")
@RequiredArgsConstructor
public class SuggestionController {

    private final OllamaService ollamaService;

    @PostMapping
    public SuggestionResponse getSuggestions(
            @RequestBody SuggestionRequest request
    ) {

        String word = request.getDetectedWord();
        String mode = request.getMode();
        String language = request.getLanguage();
        System.out.println("====== SUGGESTION CONTROLLER ======");
        System.out.println("WORD RECEIVED = " + word);
        System.out.println("MODE RECEIVED = " + mode);
        System.out.println("LANGUAGE RECEIVED = " + language);
        if (word == null || word.isBlank()) {

            return new SuggestionResponse(
                    java.util.List.of(
                            "No suggestions available"
                    )
            );
        }

        if (mode == null || mode.isBlank()) {
            mode = "general";
        }

        if (language == null || language.isBlank()) {
            language = "english";
        }

        return new SuggestionResponse(
                ollamaService.buildSuggestions(
                        word,
                        mode,
                        language
                )
        );
    }

    @PostMapping("/translate-suggestion")
    public java.util.Map<String, String> translateSuggestion(
            @RequestBody java.util.Map<String, String> request
    ) {

        String text = request.get("text");
        String language = request.get("language");

        String translated =
                ollamaService.translateText(
                        text,
                        language
                );

        return java.util.Map.of(
                "translatedText",
                translated
        );
    }
}