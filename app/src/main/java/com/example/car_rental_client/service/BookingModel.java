package com.example.car_rental_client.service;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class BookingModel {
    @SerializedName("id")
    private int bookingID;
    @SerializedName("car")
    private CarModel bookedCar;
    @SerializedName("user")
    private UserModel booker;
    @SerializedName("start_date")
    private String startDate;
    @SerializedName("end_date")
    private String endDate;

    public BookingModel(int bookingID, CarModel bookedCar, UserModel booker, String startDate, String endDate) {
        this.bookingID = bookingID;
        this.bookedCar = bookedCar;
        this.booker = booker;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getBookingID() {
        return bookingID;
    }

    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    public CarModel getBookedCar() {
        return bookedCar;
    }

    public void setBookedCar(CarModel bookedCar) {
        this.bookedCar = bookedCar;
    }

    public UserModel getBooker() {
        return booker;
    }

    public void setBooker(UserModel booker) {
        this.booker = booker;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
