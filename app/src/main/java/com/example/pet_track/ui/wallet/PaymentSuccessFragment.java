package com.example.pet_track.ui.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pet_track.R;
import com.example.pet_track.api.ApiClient;
import com.example.pet_track.api.ApiService;
import com.example.pet_track.models.response.PagingResponse;
import com.example.pet_track.models.response.WrapResponse;
import com.example.pet_track.ui.booking.BookingActivity;
import com.example.pet_track.utils.SharedPreferencesManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentSuccessFragment extends Fragment {

    private static final String TAG = "PaymentSuccessFragment";

    public PaymentSuccessFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_success, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button btnBack = view.findViewById(R.id.btnBackToWallet);

        // ✅ Call API khi vừa vào Fragment
        checkTransactionStatus();

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), BookingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void checkTransactionStatus() {
        String orderCode = SharedPreferencesManager.getInstance(requireContext()).getString("last_order_code", null);
        String token = SharedPreferencesManager.getInstance(requireContext()).getToken();

        if (orderCode == null || token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "Thiếu dữ liệu để kiểm tra giao dịch.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "orderCode or token is missing");
            return;
        }

        ApiService apiService = ApiClient.getAuthenticatedClient(token).create(ApiService.class);
        apiService.checkStatusTransaction(orderCode).enqueue(new Callback<WrapResponse<PagingResponse<String>>>() {
            @Override
            public void onResponse(Call<WrapResponse<PagingResponse<String>>> call, Response<WrapResponse<PagingResponse<String>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Check status success: " + response.body().getData().getItems());
                    Toast.makeText(getContext(), "Đã kiểm tra trạng thái giao dịch thành công.", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(TAG, "Check status failed: " + response.code());
                    Toast.makeText(getContext(), "Lỗi khi kiểm tra trạng thái giao dịch.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WrapResponse<PagingResponse<String>>> call, Throwable t) {
                Log.e(TAG, "Check status API call failed", t);
                Toast.makeText(getContext(), "Gọi API thất bại.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
