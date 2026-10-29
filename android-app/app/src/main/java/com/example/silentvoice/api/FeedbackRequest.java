package com.example.silentvoice.api;

public class FeedbackRequest {

    private long userId;
    private String mood;

    public FeedbackRequest(long userId, String mood) {
        this.userId = userId;
        this.mood = mood;
    }

    public long getUserId() {
        return userId;
    }

    public String getMood() {
        return mood;
    }
}