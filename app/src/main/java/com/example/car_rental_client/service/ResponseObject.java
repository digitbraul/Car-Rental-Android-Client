package com.example.car_rental_client.service;

// Defines a basic JSON response object with a "message" key
public class ResponseObject {
    private String message;

    public ResponseObject(String message) {
        this.message = message;
    }

    public String getJSONMessage() {
        return message;
    }

    public void setJSONMessage(String message) {
        this.message = message;
    }
}
