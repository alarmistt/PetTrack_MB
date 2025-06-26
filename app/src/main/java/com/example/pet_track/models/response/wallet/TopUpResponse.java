package com.example.pet_track.models.response.wallet;

import com.example.pet_track.models.response.UserResponse;
import com.google.gson.annotations.SerializedName;

public class TopUpResponse {
    @SerializedName("id")
    private String id;

    @SerializedName("createdTime")
    private String createdTime;

    @SerializedName("userId")
    private String userId;


    @SerializedName("amount")
    private double amount;

    @SerializedName("paymentMethod")
    private String paymentMethod;

    @SerializedName("status")
    private String status;

    @SerializedName("transactionCode")
    private String transactionCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }
}