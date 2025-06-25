package com.example.pet_track.api;

import android.content.Context;

import com.example.pet_track.utils.SharedPreferencesManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;

public class ApiServiceBuilder {
    public static <T> T buildService(Class<T> serviceClass, Context context) {
        String token = context != null ? SharedPreferencesManager.getInstance(context).getToken() : null;
        return ApiClient.getAuthenticatedClient(token).create(serviceClass);
    }
}
