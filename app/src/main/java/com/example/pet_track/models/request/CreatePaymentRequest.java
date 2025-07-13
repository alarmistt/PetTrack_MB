package com.example.pet_track.models.request;

public class CreatePaymentRequest {
    private double price;

    public CreatePaymentRequest(double price) {
        this.price = price;
    }

    public double getPrice() {
        return price;
    }
}