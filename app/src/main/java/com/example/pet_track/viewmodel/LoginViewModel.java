package com.example.pet_track.viewmodel;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pet_track.api.ApiClient;
import com.example.pet_track.api.ApiService;
import com.example.pet_track.models.request.LoginRequest;
import com.example.pet_track.models.response.LoginResponse;
import com.example.pet_track.models.response.WrapResponse;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginViewModel extends ViewModel {
    private final MutableLiveData<LoginResponse> loginResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<LoginResponse> getLoginResult() {
        return loginResult;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void login(Context context, String email, String password) {
        ApiService apiService = ApiClient.getAnonymousClient().create(ApiService.class);

        LoginRequest request = new LoginRequest(email, password);

        apiService .login(request).enqueue(new Callback<WrapResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<WrapResponse<LoginResponse>> call, Response<WrapResponse<LoginResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    loginResult.postValue(response.body().getData());
                } else {
                    String errorMessageResponse = "Unknown error";
                    if (response.errorBody() != null) {
                        try {
                            String errorJson = response.errorBody().string();
                            // Parse the error message here
                            JSONObject errorResponse = new JSONObject(errorJson);
                            errorMessageResponse = errorResponse.optString("message", "An error occurred");
                        } catch (Exception e) {
                            errorMessageResponse = "Error reading error body: " + e.getMessage();
                        }
                    }
                    errorMessage.postValue(errorMessageResponse); // Update the UI with error message from API
                }
            }

            @Override
            public void onFailure(Call<WrapResponse<LoginResponse>> call, Throwable t) {
                errorMessage.postValue("Login error: " + t.getMessage());
            }
        });
    }
}



