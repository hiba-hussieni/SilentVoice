package com.example.silentvoice;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientSpring {

    private static final String BASE_URL = "http://10.0.2.2:8081/";
    private static Retrofit retrofit;

    public static Retrofit getClient() {

        if (retrofit == null) {

            OkHttpClient client = new OkHttpClient.Builder()

                    .connectTimeout(10, TimeUnit.SECONDS)

                    .readTimeout(60, TimeUnit.SECONDS)

                    .writeTimeout(30, TimeUnit.SECONDS)

                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}