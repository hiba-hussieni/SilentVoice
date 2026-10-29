package com.example.silentvoice.api;

public class LoginResponse {

    private String status;
    private String message;

    private int userId;
    private String email;
    private String fullName;
    private String phone;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public int getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", userId=" + userId +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}