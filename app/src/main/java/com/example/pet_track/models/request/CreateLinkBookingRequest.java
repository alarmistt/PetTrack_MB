package com.example.pet_track.models.request;
public class CreateLinkBookingRequest {
    private int price;
    private String bookingId;
    private String description = "Booking";
    private String returnUrl = "https://mystic-blind-box.web.app/wallet-success";
    private String cancelUrl = "https://mystic-blind-box.web.app/wallet-fail";

    public CreateLinkBookingRequest(int price, String bookingId) {
        this.price = price;
        this.bookingId = bookingId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }
}
