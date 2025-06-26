// File: app/src/main/java/com/example/pet_track/viewmodel/WalletViewModel.java
package com.example.pet_track.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pet_track.api.ApiClient;
import com.example.pet_track.api.ApiService;
import com.example.pet_track.models.response.wallet.WalletResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletViewModel extends ViewModel {
    private final MutableLiveData<WalletResponse> walletLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public LiveData<WalletResponse> getWallet() {
        return walletLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public void fetchWallet(String token) {
        ApiService apiService = ApiClient.getAuthenticatedClient(token).create(ApiService.class);
        apiService.myWallet().enqueue(new Callback<WalletResponse>() {
            @Override
            public void onResponse(Call<WalletResponse> call, Response<WalletResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    walletLiveData.postValue(response.body());
                } else {
                    errorLiveData.postValue("Failed to load wallet");
                }
            }

            @Override
            public void onFailure(Call<WalletResponse> call, Throwable t) {
                errorLiveData.postValue(t.getMessage());
            }
        });
    }
}