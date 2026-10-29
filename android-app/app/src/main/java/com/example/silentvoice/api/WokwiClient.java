package com.example.silentvoice.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WokwiClient {

    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:8081/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}