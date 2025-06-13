package com.example.pet_track.api;

import com.example.pet_track.models.request.LoginRequest;
import com.example.pet_track.models.response.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("login-google")
    Call<LoginResponse> login(@Body LoginRequest request);
}
