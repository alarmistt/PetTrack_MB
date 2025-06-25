package com.example.pet_track.api;

import com.example.pet_track.utils.ToStringConverterFactory;
import com.example.pet_track.utils.UnsafeOkHttpClient;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://10.0.2.2:7276/";

    // Dùng cho login/register
    public static Retrofit getAnonymousClient() {
        OkHttpClient client = UnsafeOkHttpClient.getUnsafeOkHttpClient();
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    // Dùng cho các request có token
    public static Retrofit getAuthenticatedClient(String token) {
        OkHttpClient.Builder clientBuilder = UnsafeOkHttpClient.getUnsafeOkHttpClient().newBuilder();

        if (token != null) {
            clientBuilder.addInterceptor(chain -> {
                Request originalRequest = chain.request();
                Request modified = originalRequest.newBuilder()
                        .header("Authorization", "Bearer " + token)
                        .method(originalRequest.method(), originalRequest.body())
                        .build();
                return chain.proceed(modified);
            });
        }

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
