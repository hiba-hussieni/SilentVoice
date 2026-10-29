package com.example.silentvoice.api;

public class ModeResponse {

    private long id;
    private String name;
    private boolean active;
    private long userId;

    private String iconName;
    private String colorHex;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public long getUserId() {
        return userId;
    }

    public String getIconName() {
        return iconName;
    }

    public String getColorHex() {
        return colorHex;
    }
}