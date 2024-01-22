package com.example.car_rental_client.service;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BookingAPI {
    @GET("bookings")
    Call<List<BookingModel>> getBookings(); // Returns a list of (all) bookings

    @GET("bookings")
    Call<List<BookingModel>> getBookingsForUserID(
            @Query("user_id") int userID
    );

    @POST("bookings")
    @FormUrlEncoded
    Call<ResponseObject> createBooking(
            @Header("Cookie") String sessionID,
            @Field("car_id") int carID,
            @Field("user_id") int userID,
            @Field("start_date") String startDate,
            @Field("end_date") String endDate
    ); // creates new booking resource

    @PATCH("bookings/{id}")
    @FormUrlEncoded
    Call<ResponseObject> updateBooking(
            @Header("Cookie") String sessionID,
            @Path("id") int bookingID,
            @Field("start_date") String startDate,
            @Field("end_date") String endDate
    );

    @DELETE("bookings/{id}")
    Call<ResponseObject> deleteBooking(
            @Header("Cookie") String sessionID,
            @Path("id") int bookingID
    );
}
