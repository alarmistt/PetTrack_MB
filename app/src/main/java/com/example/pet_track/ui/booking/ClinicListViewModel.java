package com.example.pet_track.ui.booking;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pet_track.api.ApiClient;
import com.example.pet_track.api.ApiService;
import com.example.pet_track.models.response.ClinicResponse;
import com.example.pet_track.models.response.WrapResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClinicListViewModel extends ViewModel {

    private final MutableLiveData<List<ClinicResponse>> clinics = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final ApiService apiService;

    public ClinicListViewModel() {
        apiService = ApiClient.getClient().create(ApiService.class);
        fetchClinics();
    }

    public LiveData<List<ClinicResponse>> getClinics() {
        return clinics;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    private void fetchClinics() {
        apiService.getApprovedClinics().enqueue(new Callback<WrapResponse<List<ClinicResponse>>>() {
            @Override
            public void onResponse(Call<WrapResponse<List<ClinicResponse>>> call, Response<WrapResponse<List<ClinicResponse>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    clinics.setValue(response.body().getData());
                } else {
                    errorMessage.setValue("Failed to load clinics");
                }
            }

            @Override
            public void onFailure(Call<WrapResponse<List<ClinicResponse>>> call, Throwable t) {
                errorMessage.setValue(t.getMessage());
            }
        });
    }
} 