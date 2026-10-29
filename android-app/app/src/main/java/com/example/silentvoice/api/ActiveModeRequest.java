package com.example.silentvoice.api;

public class ActiveModeRequest {
    private long userId;
    private long modeId;

    public ActiveModeRequest(long userId, long modeId) {
        this.userId = userId;
        this.modeId = modeId;
    }
}