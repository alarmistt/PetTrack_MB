package com.example.pet_track.viewmodel;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.pet_track.api.ApiService;
import com.example.pet_track.api.ApiServiceBuilder;
import com.example.pet_track.models.request.RegisterRequest;
import com.example.pet_track.models.response.RegisterResponse;
import com.example.pet_track.models.response.WrapResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterViewModel extends ViewModel {
    private final MutableLiveData<RegisterResponse> registerResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<RegisterResponse> getRegisterResult() {
        return registerResult;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void register(Context context, RegisterRequest request) {
        ApiService api = ApiServiceBuilder.buildService(ApiService.class, context);

        api.register(request).enqueue(new Callback<WrapResponse<RegisterResponse>>() {
            @Override
            public void onResponse(Call<WrapResponse<RegisterResponse>> call, Response<WrapResponse<RegisterResponse>> response) {
                if (response.isSuccessful()) {
                    WrapResponse<RegisterResponse> body = response.body();

                    if (body != null && body.getData() != null) {
                        registerResult.postValue(body.getData());
                    } else {
                        Log.e("RegisterViewModel", "Success but data is null — likely due to parsing issue");
                        errorMessage.postValue("Registration failed: data is null");
                    }

                } else {
                    String errorBody = "";
                    errorMessage.postValue("Registration failed: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<WrapResponse<RegisterResponse>> call, Throwable t) {
                errorMessage.postValue("Registration error: " + t.getMessage());
            }
        });
    }
}

