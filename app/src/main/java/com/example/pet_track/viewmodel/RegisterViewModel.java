package com.example.pet_track.viewmodel;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pet_track.api.ApiClient;
import com.example.pet_track.api.ApiService;
import com.example.pet_track.models.request.RegisterRequest;
import com.example.pet_track.models.response.RegisterResponse;
import com.example.pet_track.models.response.WrapResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

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
        ApiService apiService  = ApiClient.getAnonymousClient().create(ApiService.class);

        apiService .register(request).enqueue(new Callback<WrapResponse<RegisterResponse>>() {
            @Override
            public void onResponse(Call<WrapResponse<RegisterResponse>> call, Response<WrapResponse<RegisterResponse>> response) {
                if (response.isSuccessful()) {
                    WrapResponse<RegisterResponse> body = response.body();

                    if (body != null && body.getData() != null) {
                        registerResult.postValue(body.getData());
                    } else {
                        Log.e("RegisterViewModel", "Success but data is null — likely due to parsing issue");
                        errorMessage.postValue("Đăng ký thất bại: dữ liệu phản hồi rỗng");
                    }

                } else {
                    try {
                        String errorJson = response.errorBody().string();
                        JSONObject errorResponse = new JSONObject(errorJson);
                        StringBuilder errorMessageBuilder = new StringBuilder();

                        if (errorResponse.has("errors")) {
                            JSONObject errors = errorResponse.getJSONObject("errors");

                            for (Iterator<String> it = errors.keys(); it.hasNext(); ) {
                                String key = it.next();
                                JSONArray messages = errors.getJSONArray(key);

                                for (int i = 0; i < messages.length(); i++) {
                                    errorMessageBuilder.append(key)
                                            .append(": ")
                                            .append(messages.getString(i))
                                            .append("\n");
                                }
                            }
                        } else {
                            errorMessageBuilder.append("Đã xảy ra lỗi không xác định");
                        }

                        errorMessage.postValue(errorMessageBuilder.toString().trim());

                    } catch (Exception e) {
                        errorMessage.postValue("Lỗi xử lý phản hồi: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<WrapResponse<RegisterResponse>> call, Throwable t) {
                errorMessage.postValue("Registration error: " + t.getMessage());
            }
        });
    }
}

