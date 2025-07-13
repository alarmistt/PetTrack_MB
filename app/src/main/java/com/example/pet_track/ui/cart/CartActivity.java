package com.example.pet_track.ui.cart;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pet_track.R;
import com.example.pet_track.api.ApiClient;
import com.example.pet_track.api.ApiService;
import com.example.pet_track.models.response.BookingHistoryResponse;
import com.example.pet_track.models.response.PagingResponse;
import com.example.pet_track.models.response.WrapResponse;
import com.example.pet_track.utils.SharedPreferencesManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    private LinearLayout cartItemsContainer;
    private TextView totalPriceText;
    private Button btnCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartItemsContainer = findViewById(R.id.cart_items_container);
        totalPriceText = findViewById(R.id.total_price);
        btnCheckout = findViewById(R.id.btn_checkout);

        fetchPendingBookings();
    }

    private void fetchPendingBookings() {
        String userId = SharedPreferencesManager.getInstance(this).getUserId();
        String token = SharedPreferencesManager.getInstance(this).getToken();

        if (userId == null || token == null) {
            Toast.makeText(this, "Bạn chưa đăng nhập.", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getAuthenticatedClient(token).create(ApiService.class);
        apiService.getBookings(1, 50, null, userId, "Pending").enqueue(new Callback<WrapResponse<PagingResponse<BookingHistoryResponse>>>() {
            @Override
            public void onResponse(Call<WrapResponse<PagingResponse<BookingHistoryResponse>>> call,
                                   Response<WrapResponse<PagingResponse<BookingHistoryResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<BookingHistoryResponse> bookings = response.body().getData().getItems();
                    displayBookings(bookings);
                } else {
                    Toast.makeText(CartActivity.this, "Lỗi khi tải giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WrapResponse<PagingResponse<BookingHistoryResponse>>> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayBookings(List<BookingHistoryResponse> bookings) {
        cartItemsContainer.removeAllViews();
        BigDecimal total = BigDecimal.ZERO;

        for (BookingHistoryResponse booking : bookings) {
            View itemView = getLayoutInflater().inflate(R.layout.item_cart_booking, null);

            TextView serviceName = itemView.findViewById(R.id.service_name);
            TextView clinicName = itemView.findViewById(R.id.clinic_name);
            TextView date = itemView.findViewById(R.id.booking_date);
            TextView price = itemView.findViewById(R.id.booking_price);

            serviceName.setText(booking.getServicePackageName());
            clinicName.setText("Tại: " + booking.getClinicName());
            date.setText("Ngày: " + booking.getAppointmentDate());
            price.setText(String.format(Locale.GERMAN, "%,.0f VND", booking.getPrice()));

            total = total.add(BigDecimal.valueOf(booking.getPrice()));
            cartItemsContainer.addView(itemView);
        }

        totalPriceText.setText(String.format(Locale.GERMAN, "Tổng: %,.0f VND", total));
    }
}
