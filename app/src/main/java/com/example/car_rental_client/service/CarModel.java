package com.example.car_rental_client.service;

import com.google.gson.annotations.SerializedName;

public class CarModel {
    @SerializedName("id")
    private int carID;
    @SerializedName("car_make")
    private String carMake;
    @SerializedName("car_model")
    private String carModel;
    @SerializedName("seats")
    private int seats;
    @SerializedName("fuel_type")
    private String fuelType;
    @SerializedName("thumb_url")
    private String thumbURL;
    @SerializedName("location")
    private String location;
    @SerializedName("daily_price")
    private float dailyPrice;
    @SerializedName("deductible")
    private float deductible;
    @SerializedName("transmission")
    private String transmission;

    public CarModel(int carID, String carMake, String carModel, int seats, String fuelType, String thumbURL, String location, float dailyPrice, float deductible, String transmission) {
        this.carID = carID;
        this.carMake = carMake;
        this.carModel = carModel;
        this.seats = seats;
        this.fuelType = fuelType;
        this.thumbURL = thumbURL;
        this.location = location;
        this.dailyPrice = dailyPrice;
        this.deductible = deductible;
        this.transmission = transmission;
    }

    public int getCarID() {
        return carID;
    }

    public void setCarID(int carID) {
        this.carID = carID;
    }

    public String getCarMake() {
        return carMake;
    }

    public void setCarMake(String carMake) {
        this.carMake = carMake;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getThumbURL() {
        return thumbURL;
    }

    public void setThumbURL(String thumbURL) {
        this.thumbURL = thumbURL;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public float getDailyPrice() {
        return dailyPrice;
    }

    public void setDailyPrice(float dailyPrice) {
        this.dailyPrice = dailyPrice;
    }

    public float getDeductible() {
        return deductible;
    }

    public void setDeductible(float deductible) {
        this.deductible = deductible;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }
}
