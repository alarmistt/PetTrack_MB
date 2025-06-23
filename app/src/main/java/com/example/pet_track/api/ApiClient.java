package com.example.pet_track.api;

import com.example.pet_track.utils.ToStringConverterFactory;
import com.example.pet_track.utils.UnsafeOkHttpClient;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://10.0.2.2:7276/";
    private static Retrofit retrofit;

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient client = UnsafeOkHttpClient.getUnsafeOkHttpClient();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit buildUnsafeRetrofitWithToken(String token) {
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
