package com.Senior.Golves_Project.dto;

public class SuggestionRequest {

    private String detectedWord;
    private String mode;
    private String language;

    public String getDetectedWord() {
        return detectedWord;
    }

    public void setDetectedWord(String detectedWord) {
        this.detectedWord = detectedWord;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}