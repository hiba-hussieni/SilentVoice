package com.example.silentvoice.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    // ================= AUTH =================

    @POST("api/auth/register")
    Call<UserResponse> register(@Body RegisterRequest request);

    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @PUT("api/auth/update-profile")
    Call<UserResponse> updateProfile(
            @Body UpdateProfileRequest request
    );

    @DELETE("api/auth/delete/{userId}")
    Call<LoginResponse> deleteUser(
            @Path("userId") long userId
    );

    // ================= FEEDBACK =================

    @POST("api/feedback/submit")
    Call<FeedbackResponse> submitFeedback(
            @Body FeedbackRequest request
    );

    // ================= MODES =================

    @PUT("api/modes/active")
    Call<ModeResponse> setActiveMode(
            @Body ActiveModeRequest request
    );

    @GET("api/modes/user/{userId}")
    Call<List<ModeResponse>> getUserModes(
            @Path("userId") long userId
    );

    @DELETE("api/modes/user/{userId}/name/{modeName}")
    Call<Void> deleteMode(
            @Path("userId") long userId,
            @Path("modeName") String modeName
    );

    // ================= TRANSLATION =================

    @POST("api/sensors/translate")
    Call<TranslationResponse> translate(
            @Body SensorDataRequest request
    );

    @POST("api/gestures/add-mode-with-gesture")
    Call<TranslationResponse> addModeWithGesture(
            @Body AddModeWithGestureRequest request
    );
    @POST("api/suggestions")
    Call<SuggestionResponse> getSuggestions(
            @Body SuggestionRequest request
    );
}