package com.example.apitest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {
    // https 전용 — http 는 503 을 돌려준다
    private static String BASE_URL = "https://openapi.gg.go.kr";

    public static RetrofitService create() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(RetrofitService.class);
    }
}
