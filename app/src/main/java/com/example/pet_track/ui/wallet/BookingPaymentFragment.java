package com.example.pet_track.ui.wallet;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pet_track.R;
import com.example.pet_track.api.ApiClient;
import com.example.pet_track.api.ApiService;
import com.example.pet_track.models.request.CreateLinkBookingRequest;
import com.example.pet_track.models.response.WrapResponse;
import com.example.pet_track.models.response.payment.CreatePaymentResult;
import com.example.pet_track.utils.SharedPreferencesManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingPaymentFragment extends Fragment {

    private static final String TAG = "BookingPaymentFragment";
    private static final String SUCCESS_URL = "https://mystic-blind-box.web.app/wallet-success";
    private static final String FAIL_URL = "https://mystic-blind-box.web.app/wallet-fail";

    private static final String ARG_BOOKING_ID = "booking_id";
    private static final String ARG_AMOUNT = "amount";

    private String bookingId;
    private int amount;

    public static BookingPaymentFragment newInstance(String bookingId, int amount) {
        BookingPaymentFragment fragment = new BookingPaymentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BOOKING_ID, bookingId);
        args.putInt(ARG_AMOUNT, amount);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_booking_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            bookingId = getArguments().getString(ARG_BOOKING_ID);
            amount = getArguments().getInt(ARG_AMOUNT);
        } else {
            Toast.makeText(getContext(), "Thiếu dữ liệu booking.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Missing arguments for bookingId or amount");
            return;
        }

        WebView webView = view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);

        String token = SharedPreferencesManager.getInstance(requireContext()).getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "Không tìm thấy token. Hãy đăng nhập lại.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Token is null or empty → Có thể chưa đăng nhập hoặc chưa lưu token.");
            return;
        }

        ApiService apiService = ApiClient.getAuthenticatedClient(token).create(ApiService.class);
        CreateLinkBookingRequest req = new CreateLinkBookingRequest(amount, bookingId);

        apiService.createBookingPayment(req).enqueue(new Callback<WrapResponse<CreatePaymentResult>>() {
            @Override
            public void onResponse(Call<WrapResponse<CreatePaymentResult>> call, Response<WrapResponse<CreatePaymentResult>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    String url = response.body().getData().getCheckoutUrl();
                    String orderCode = response.body().getData().getOrderCode() +"";

                    Log.d(TAG, "OrderCode: " + orderCode);

// Lưu lại để sử dụng trong PaymentSuccessFragment
                    SharedPreferencesManager.getInstance(requireContext())
                            .putString("last_order_code", orderCode);
                    Log.d(TAG, "Checkout URL received: " + url);

                    webView.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                            String newUrl = request.getUrl().toString();
                            Log.d(TAG, "Redirected URL: " + newUrl);

                            if (newUrl.startsWith(SUCCESS_URL)) {
                                showResultFragment(true);
                                return true;
                            } else if (newUrl.startsWith(FAIL_URL)) {
                                showResultFragment(false);
                                return true;
                            }
                            return false;
                        }
                    });

                    webView.loadUrl(url);
                } else {
                    String error = "Code: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            error += " - " + response.errorBody().string();
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading errorBody", e);
                        }
                    }
                    Toast.makeText(getContext(), "Không lấy được link thanh toán.\n" + error, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Response failed or empty body: " + error);
                }
            }

            @Override
            public void onFailure(Call<WrapResponse<CreatePaymentResult>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi khi tạo link thanh toán.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "API failure: ", t);
            }
        });
    }

    private void showResultFragment(boolean isSuccess) {
        Fragment fragment = isSuccess ? new PaymentSuccessFragment() : new PaymentFailFragment();
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment_content_main, fragment)
                .addToBackStack(null)
                .commit();
    }
}
