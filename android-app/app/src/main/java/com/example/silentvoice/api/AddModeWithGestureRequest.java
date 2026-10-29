package com.example.silentvoice.api;

public class AddModeWithGestureRequest {

    public int userId;
    public String modeName;
    public String word;

    public String thumbState;
    public String indexState;
    public String middleState;
    public String ringState;
    public String pinkyState;

    public int accX;
    public int accY;
    public int accZ;

    public int gyroX;
    public int gyroY;
    public int gyroZ;

    public AddModeWithGestureRequest(
            int userId,
            String modeName,
            String word,
            String thumbState,
            String indexState,
            String middleState,
            String ringState,
            String pinkyState,
            int accX,
            int accY,
            int accZ,
            int gyroX,
            int gyroY,
            int gyroZ
    ) {

        this.userId = userId;
        this.modeName = modeName;
        this.word = word;

        this.thumbState = thumbState;
        this.indexState = indexState;
        this.middleState = middleState;
        this.ringState = ringState;
        this.pinkyState = pinkyState;

        this.accX = accX;
        this.accY = accY;
        this.accZ = accZ;

        this.gyroX = gyroX;
        this.gyroY = gyroY;
        this.gyroZ = gyroZ;
    }
}