package com.example.silentvoice.api;

public class UpdateProfileRequest {

    private long userId;
    private String fullName;
    private String email;
    private String phone;

    public UpdateProfileRequest(
            long userId,
            String fullName,
            String email,
            String phone
    ) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }
}