package com.example.pet_track.models.response;

public class UserUpdateRequest {
    private String fullName;
    private String address;
    private String phoneNumber;
    private String avatarUrl;

    public UserUpdateRequest(String fullName, String address, String phoneNumber, String avatarUrl) {
        this.fullName = fullName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.avatarUrl = avatarUrl;
    }

    // Getter và Setter
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}