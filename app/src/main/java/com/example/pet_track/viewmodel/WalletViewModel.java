package com.example.pet_track.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pet_track.api.ApiClient;
import com.example.pet_track.api.ApiService;
import com.example.pet_track.models.response.PagingResponse;
import com.example.pet_track.models.response.WrapResponse;
import com.example.pet_track.models.response.wallet.TopUpResponse;
import com.example.pet_track.models.response.wallet.WalletResponse;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletViewModel extends ViewModel {
    private final MutableLiveData<WalletResponse> walletLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<TopUpResponse>> topUpHistoryLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public LiveData<WalletResponse> getWallet() {
        return walletLiveData;
    }

    public LiveData<List<TopUpResponse>> getTopUpHistory() {
        return topUpHistoryLiveData;
    }

    public LiveData<String> getError() {
        return errorLiveData;
    }

    public void fetchWallet(String token) {
        ApiService apiService = ApiClient.getAuthenticatedClient(token).create(ApiService.class);
        apiService.myWallet().enqueue(new Callback<WrapResponse<WalletResponse>>() {
            @Override
            public void onResponse(Call<WrapResponse<WalletResponse>> call, Response<WrapResponse<WalletResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    walletLiveData.postValue(response.body().getData());
                } else {
                    errorLiveData.postValue("Không thể tải thông tin ví");
                }
            }

            @Override
            public void onFailure(Call<WrapResponse<WalletResponse>> call, Throwable t) {
                errorLiveData.postValue("Lỗi khi tải ví: " + t.getMessage());
            }
        });
    }

    public void fetchTopUpHistory(String token, int pageIndex, int pageSize, String userId, String status) {
        ApiService apiService = ApiClient.getAuthenticatedClient(token).create(ApiService.class);
        apiService.getTopUpTransaction(pageIndex, pageSize, userId, status)
                .enqueue(new Callback<WrapResponse<PagingResponse<TopUpResponse>>>() {
                    @Override
                    public void onResponse(Call<WrapResponse<PagingResponse<TopUpResponse>>> call,
                                           Response<WrapResponse<PagingResponse<TopUpResponse>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            PagingResponse<TopUpResponse> paging = response.body().getData();
                            if (paging != null && paging.getItems() != null) {
                                topUpHistoryLiveData.postValue(paging.getItems());
                            } else {
                                errorLiveData.postValue("Không có dữ liệu giao dịch");
                            }
                        } else {
                            errorLiveData.postValue("Không tải được lịch sử giao dịch");
                        }
                    }

                    @Override
                    public void onFailure(Call<WrapResponse<PagingResponse<TopUpResponse>>> call, Throwable t) {
                        errorLiveData.postValue("Lỗi khi tải lịch sử: " + t.getMessage());
                    }
        });
    }
}
