package com.example.silentvoice.api;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiServiceSpring {

    @POST("api/sensors/translate")
    Call<TranslationResponse> translate(
            @Body SensorDataRequest request
    );

    @POST("api/suggestions")
    Call<SuggestionResponse> getSuggestions(
            @Body SuggestionRequest request
    );

    @POST("api/suggestions/translate-suggestion")
    Call<Map<String, String>> translateSuggestion(
            @Body Map<String, String> request
    );
}