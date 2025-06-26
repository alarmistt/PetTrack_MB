package com.example.pet_track.ui.booking;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pet_track.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import android.widget.Toast;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;
import java.io.IOException;

import com.example.pet_track.api.ApiClient;
import com.example.pet_track.api.ApiService;
import com.example.pet_track.models.request.BookingRequest;
import com.example.pet_track.models.response.WrapResponse;
import com.example.pet_track.utils.SharedPreferencesManager;

public class PaymentBookingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_booking);

        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        // Get data from Intent
        String serviceName = getIntent().getStringExtra("serviceName");
        double servicePrice = getIntent().getDoubleExtra("servicePrice", 0);
        String note = getIntent().getStringExtra("note");
        long dateInMillis = getIntent().getLongExtra("date", 0);
        String slotText = getIntent().getStringExtra("slotText");

        // Get IDs for API call
        String slotId = getIntent().getStringExtra("slotId");
        String servicePackageId = getIntent().getStringExtra("servicePackageId");

        // Find views
        TextView serviceNameTv = findViewById(R.id.service_package_name);
        TextView servicePriceTv = findViewById(R.id.service_package_price);
        TextView noteTv = findViewById(R.id.booking_note);
        TextView dateTv = findViewById(R.id.booking_date);
        TextView slotTv = findViewById(R.id.booking_slot);
        TextView serviceFeeTv = findViewById(R.id.service_fee);
        TextView totalFeeTv = findViewById(R.id.total_fee);

        // Date formatting
        Date date = new Date(dateInMillis);
        SimpleDateFormat displaySdf = new SimpleDateFormat("EEEE, dd 'tháng' MM, yyyy", new Locale("vi"));
        String displayDate = displaySdf.format(date);

        // Calculate fees
        double total = servicePrice;

        // Format numbers
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedPrice = currencyFormat.format(servicePrice).replace("₫", "vnd");
        String formattedServiceFee = currencyFormat.format(servicePrice).replace(",00 ₫", "");
        String formattedTotal = currencyFormat.format(total).replace(",00 ₫", "");

        // Set data to views
        serviceNameTv.setText(serviceName);
        servicePriceTv.setText(formattedPrice);
        noteTv.setText("Ghi chú: " + note);
        dateTv.setText("Ngày: " + displayDate);
        slotTv.setText("Giờ: " + slotText);
        serviceFeeTv.setText(formattedServiceFee);
        totalFeeTv.setText(formattedTotal);

        // Handle continue button click if needed
        Button btnContinue = findViewById(R.id.btn_continue);
        btnContinue.setOnClickListener(v -> {
            // Format date for API
            SimpleDateFormat apiSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            apiSdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String apiDate = apiSdf.format(date);

            // Create request
            BookingRequest request = new BookingRequest(slotId, servicePackageId, apiDate);
            
            // Get token
            SharedPreferencesManager sharedPreferencesManager = SharedPreferencesManager.getInstance(this);
            String token = sharedPreferencesManager.getToken();

            if (token == null) {
                Toast.makeText(this, "Yêu cầu đăng nhập để đặt lịch.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Call API
            ApiService apiService = ApiClient.getAuthenticatedClient(token).create(ApiService.class);
            apiService.createBooking(request).enqueue(new Callback<WrapResponse<Object>>() {
                @Override
                public void onResponse(Call<WrapResponse<Object>> call, Response<WrapResponse<Object>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(PaymentBookingActivity.this, "Đặt lịch thành công!", Toast.LENGTH_SHORT).show();
                        // You might want to navigate to a success screen or booking history
                        finish(); // Close the payment screen
                    } else {
                        String errorMessage = "Đặt lịch thất bại.";
                        if (response.errorBody() != null) {
                            try {
                                String errorBodyStr = response.errorBody().string();
                                Log.e("BookingAPI", "Lỗi đặt lịch: " + response.code() + " - " + errorBodyStr);
                                errorMessage += " Mã lỗi: " + response.code();
                            } catch (IOException e) {
                                Log.e("BookingAPI", "Lỗi đọc error body", e);
                            }
                        }
                        Toast.makeText(PaymentBookingActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<WrapResponse<Object>> call, Throwable t) {
                    Log.e("BookingAPI", "Lỗi kết nối API", t);
                    Toast.makeText(PaymentBookingActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
} 