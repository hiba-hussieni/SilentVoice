package com.example.silentvoice.api;

import com.google.gson.annotations.SerializedName;

public class TranslationResponse {
    private String status;
    private String fingerPattern;
    private String gesture;
    private String meaning;
    private String sentence;
    private String confidence;
    private String mode;

    @SerializedName("recognized")
    private boolean recognized;

    public String getStatus() { return status; }
    public String getFingerPattern() { return fingerPattern; }
    public String getGesture() { return gesture; }
    public String getMeaning() { return meaning; }
    public String getSentence() { return sentence; }
    public String getConfidence() { return confidence; }
    public String getMode() { return mode; }
    public boolean isRecognized() { return recognized; }
}