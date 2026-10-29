package com.example.silentvoice.api;

public class SuggestionRequest {

    private String detectedWord;
    private String mode;
    private String language;

    public SuggestionRequest(String detectedWord, String mode, String language) {
        this.detectedWord = detectedWord;
        this.mode = mode;
        this.language = language;
    }

    public String getDetectedWord() {
        return detectedWord;
    }

    public String getMode() {
        return mode;
    }

    public String getLanguage() {
        return language;
    }
}