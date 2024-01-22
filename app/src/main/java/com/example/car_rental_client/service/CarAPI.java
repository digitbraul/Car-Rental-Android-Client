package com.example.car_rental_client.service;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CarAPI {
    @GET("cars")
    Call<List<CarModel>> getCars(); // Returns a list of cars
    //TODO: filtered requests with @Query and search menu

    @GET("cars/{id}")
    Call<CarModel> getData(@Path("id") int carID); // Returns a single car resource

    @POST("cars")
    @FormUrlEncoded
    Call<ResponseObject> createCar(
            @Field("id") int carID,
            @Field("car_make") String carMake,
            @Field("car_model") String carModel,
            @Field("seats") int seats,
            @Field("fuel_type") String fuelType,
            @Field("thumb_url") String thumbURL,
            @Field("location") String location,
            @Field("daily_price") float dailyPrice,
            @Field("deductible") float deductible,
            @Field("transmission") String transmission
    ); // only accessible for admin, returns a json message with "Car added successfully"
}
