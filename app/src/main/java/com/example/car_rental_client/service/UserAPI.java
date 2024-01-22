package com.example.car_rental_client.service;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserAPI {
    @GET("users")
    Call<List<UserModel>> getUsers();

    @POST("login")
    @FormUrlEncoded
    Call<ResponseObject> login(
            @Field("uname") String username,
            @Field("upass") String password
    );

    @POST("users")
    @FormUrlEncoded
    Call<ResponseObject> signUp(
            @Field("uname") String username,
            @Field("upass") String password
    );

    @GET("logout")
    Call<ResponseObject> logout(@Header("Cookie") String sessionID);

    @PATCH("users/{uname}")
    @FormUrlEncoded
    Call<ResponseObject> updatePassword(
            @Header("Cookie") String sessionID,
            @Path("uname") String username,
            @Field("upass") String password
    );
}
