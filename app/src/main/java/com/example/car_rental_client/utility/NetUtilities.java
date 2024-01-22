package com.example.car_rental_client.utility;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetUtilities {
    public static final String BASE_URL = "http://10.0.2.2:5000";
    public static Retrofit CreateRetrofitObject() {
        return new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
