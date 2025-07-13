package com.example.pet_track.models.response;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("token")
    private String accessToken;  // JWT Token trả về từ API
    private UserResponse userResponse;  // Thông tin người dùng

    public LoginResponse(String accessToken, UserResponse userResponse) {
        this.accessToken = accessToken;
        this.userResponse = userResponse;
    }

    public String getAccessToken() {
        return accessToken;
    }
    public void setToken(String token) {
        this.accessToken = token;
    }
    public UserResponse getUserResponse() {
        return userResponse;
    }
    public void setUserResponse(UserResponse userResponse) {
        this.userResponse = userResponse;
    }
}
