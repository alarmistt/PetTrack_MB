package com.example.pet_track.ui.booking;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pet_track.api.ApiClient;
import com.example.pet_track.api.ApiService;
import com.example.pet_track.models.response.ClinicResponse;
import com.example.pet_track.models.response.Slot;
import com.example.pet_track.models.response.WrapResponse;
import com.example.pet_track.utils.SharedPreferencesManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingViewModel extends ViewModel {

    private final MutableLiveData<List<Slot>> slots = new MutableLiveData<>();
    private final MutableLiveData<ClinicResponse> clinicDetails = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<List<Slot>> getSlots() {
        return slots;
    }

    public LiveData<ClinicResponse> getClinicDetails() {
        return clinicDetails;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void getAvailableSlots(Context context, String clinicId, String date) {
        SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(context);
        String token = sharedPreferencesManager.getToken();

        if (token == null) {
            errorMessage.postValue("Yêu cầu đăng nhập để xem khung giờ.");
            return;
        }

        ApiService apiService = ApiClient.getAuthenticatedClient(token).create(ApiService.class);
        apiService.getAvailableSlots(clinicId, date).enqueue(new Callback<WrapResponse<List<Slot>>>() {
            @Override
            public void onResponse(Call<WrapResponse<List<Slot>>> call, Response<WrapResponse<List<Slot>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    slots.postValue(response.body().getData());
                } else {
                    errorMessage.postValue("Failed to get slots: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<WrapResponse<List<Slot>>> call, Throwable t) {
                errorMessage.postValue("Error getting slots: " + t.getMessage());
            }
        });
    }

    public void getClinicDetails(String clinicId) {
        ApiService apiService = ApiClient.getAnonymousClient().create(ApiService.class);
        apiService.getClinicDetails(clinicId).enqueue(new Callback<WrapResponse<ClinicResponse>>() {
            @Override
            public void onResponse(Call<WrapResponse<ClinicResponse>> call, Response<WrapResponse<ClinicResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    clinicDetails.postValue(response.body().getData());
                } else {
                    errorMessage.postValue("Failed to get clinic details: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<WrapResponse<ClinicResponse>> call, Throwable t) {
                errorMessage.postValue("Error getting clinic details: " + t.getMessage());
            }
        });
    }
} 