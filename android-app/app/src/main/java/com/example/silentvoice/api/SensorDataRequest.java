package com.example.silentvoice.api;

public class SensorDataRequest {

    private long userId;
    private long modeId;

    private int thumb;
    private int index;
    private int middle;
    private int ring;
    private int pinky;

    private boolean sos;

    private double accX;
    private double accY;
    private double accZ;

    private double gyroX;
    private double gyroY;
    private double gyroZ;

    private String source;

    public SensorDataRequest(
            long userId,
            long modeId,
            int thumb,
            int index,
            int middle,
            int ring,
            int pinky,
            boolean sos,
            double accX,
            double accY,
            double accZ,
            double gyroX,
            double gyroY,
            double gyroZ
    ) {

        this.userId = userId;
        this.modeId = modeId;

        this.thumb = thumb;
        this.index = index;
        this.middle = middle;
        this.ring = ring;
        this.pinky = pinky;

        this.sos = sos;

        this.accX = accX;
        this.accY = accY;
        this.accZ = accZ;

        this.gyroX = gyroX;
        this.gyroY = gyroY;
        this.gyroZ = gyroZ;

        this.source = "ANDROID";
    }
}