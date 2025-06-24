package com.example.pet_track.api;

import com.example.pet_track.models.request.LoginRequest;
import com.example.pet_track.models.request.RegisterRequest;
import com.example.pet_track.models.response.LoginResponse;
import com.example.pet_track.models.response.RegisterResponse;
import com.example.pet_track.models.response.WrapResponse;
import com.example.pet_track.models.response.wallet.WalletResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @POST("api/Authentication/login")
    Call<WrapResponse<LoginResponse>> login(@Body LoginRequest request);
    @POST("api/Authentication/register")
    Call<WrapResponse<RegisterResponse>> register(@Body RegisterRequest request);
    @GET("api/Wallet/me")
    Call<WalletResponse> myWallet();
}
