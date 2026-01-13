package com.example.myapplication.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static RetrofitClient instance;
    private TMDBApiService apiService;

    private RetrofitClient() {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(TMDBApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        apiService = retrofit.create(TMDBApiService.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public TMDBApiService getApiService() {
        return apiService;
    }
}
