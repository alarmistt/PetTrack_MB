package com.example.pet_track.ui.booking;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.pet_track.R;
import com.example.pet_track.api.ApiClient;
import com.example.pet_track.api.ApiService;
import com.example.pet_track.models.response.BookingHistoryResponse;
import com.example.pet_track.models.response.PagingResponse;
import com.example.pet_track.models.response.ServicePackage;
import com.example.pet_track.models.response.Slot;
import com.example.pet_track.models.response.WrapResponse;
import com.example.pet_track.ui.cart.CartActivity;
import com.example.pet_track.utils.SharedPreferencesManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateBookingActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private RadioGroup radioGroupServices;
    private GridLayout timeSlotGrid;
    private TextView dateTextView;
    private Button btnXacNhan;
    private Button btnThemGoiDichVu;
    private BookingViewModel bookingViewModel;
    private String clinicId;
    private Button selectedSlotButton = null;
    private Slot selectedSlot = null;
    private List<ServicePackage> servicePackages = new ArrayList<>();
    private Calendar selectedCalendar = Calendar.getInstance();
    private ImageView iconCart;
    private TextView cartBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_booking);

        clinicId = getIntent().getStringExtra("clinicId");
        if (clinicId == null || clinicId.isEmpty()) {
            Toast.makeText(this, "Clinic ID is missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        iconCart = findViewById(R.id.icon_cart_booking);
        cartBadge = findViewById(R.id.cart_badge);
        updateCartBadge();

        iconCart.setOnClickListener(v -> {
            Intent intent = new Intent(CreateBookingActivity.this, CartActivity.class);
            startActivity(intent);
        });


        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        calendarView = findViewById(R.id.calendarView);
        radioGroupServices = findViewById(R.id.radioGroup);
        timeSlotGrid = findViewById(R.id.time_slot_grid);
        dateTextView = findViewById(R.id.dateTextView);
        btnXacNhan = findViewById(R.id.btnXacNhan);
        btnThemGoiDichVu = findViewById(R.id.btnThemGoiDichVu);

        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        setupObservers();
        updateDateDisplay(selectedCalendar);

        // Fetch initial data
        fetchSlotsForDate(selectedCalendar);
        bookingViewModel.getClinicDetails(clinicId);


        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedCalendar.set(year, month, dayOfMonth);
            updateDateDisplay(selectedCalendar);
            fetchSlotsForDate(selectedCalendar);
        });

        btnXacNhan.setOnClickListener(v -> {
            int selectedServiceId = radioGroupServices.getCheckedRadioButtonId();
            if (selectedSlot == null || selectedServiceId == -1) {
                Toast.makeText(CreateBookingActivity.this, "Vui lòng chọn khung giờ và gói dịch vụ.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Find selected service
            int selectedIndex = radioGroupServices.indexOfChild(findViewById(selectedServiceId));
            ServicePackage selectedService = servicePackages.get(selectedIndex);

            String note = ""; // Không có editNote nên sử dụng chuỗi rỗng

            Intent intent = new Intent(CreateBookingActivity.this, PaymentBookingActivity.class);
            intent.putExtra("serviceName", selectedService.getName());
            intent.putExtra("servicePrice", selectedService.getPrice());
            intent.putExtra("servicePackageId", selectedService.getId());
            intent.putExtra("note", note);
            intent.putExtra("date", selectedCalendar.getTimeInMillis());
            intent.putExtra("slotText", selectedSlotButton.getText().toString());
            intent.putExtra("slotId", selectedSlot.getId());
            startActivity(intent);
        });

        btnThemGoiDichVu.setOnClickListener(v -> {
            int selectedServiceId = radioGroupServices.getCheckedRadioButtonId();
            if (selectedSlot == null || selectedServiceId == -1) {
                Toast.makeText(CreateBookingActivity.this, "Vui lòng chọn khung giờ và gói dịch vụ.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tìm gói dịch vụ được chọn
            int selectedIndex = radioGroupServices.indexOfChild(findViewById(selectedServiceId));
            ServicePackage selectedService = servicePackages.get(selectedIndex);

            // Format ngày cho API
            SimpleDateFormat apiSdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            apiSdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            String apiDate = apiSdf.format(selectedCalendar.getTime());

            // Gửi request
            bookingViewModel.createBooking(CreateBookingActivity.this, selectedSlot.getId(), selectedService.getId(), apiDate,
                    () -> {
                        Toast.makeText(this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                        updateCartBadge();
                    },
                    error -> {
                        Toast.makeText(this, "Thêm vào giỏ hàng thất bại: " + error, Toast.LENGTH_SHORT).show();
                    }
            );
        });

    }

    private void setupObservers() {
        bookingViewModel.getSlots().observe(this, this::updateSlotsUI);
        bookingViewModel.getClinicDetails().observe(this, clinicResponse -> {
            if (clinicResponse != null && clinicResponse.getServicePackages() != null) {
                this.servicePackages.clear();
                this.servicePackages.addAll(clinicResponse.getServicePackages());
                updateServicesUI(this.servicePackages);
            }
        });
        bookingViewModel.getErrorMessage().observe(this, error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show());
    }

    private void fetchSlotsForDate(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = sdf.format(calendar.getTime());
        bookingViewModel.getAvailableSlots(this, clinicId, date);
    }

    private void updateDateDisplay(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd 'tháng' MM, yyyy", new Locale("vi"));
        String currentDate = sdf.format(calendar.getTime());
        dateTextView.setText(currentDate);
    }

    private void updateSlotsUI(List<Slot> slots) {
        timeSlotGrid.removeAllViews();
        selectedSlotButton = null;
        selectedSlot = null;
        if (slots == null || slots.isEmpty()) {
            TextView noSlotsText = new TextView(this);
            noSlotsText.setText("Không có khung giờ trống.");
            timeSlotGrid.addView(noSlotsText);
            return;
        }

        for (Slot slot : slots) {
            Button slotButton = new Button(this);

            // Format thời gian chỉ hiển thị hh:mm
            String startTime = formatTimeToHHMM(slot.getStartTime());
            String endTime = formatTimeToHHMM(slot.getEndTime());
            String slotText = startTime + " - " + endTime;
            slotButton.setText(slotText);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(8, 8, 8, 8);
            slotButton.setLayoutParams(params);

            // Đặt background và trạng thái dựa trên status
            boolean isActive = slot.getStatus() != null && slot.getStatus().equalsIgnoreCase("Active");

            if (isActive) {
                // Slot active - màu trắng, có thể chọn
                slotButton.setBackgroundResource(R.drawable.slot_button_background);
                slotButton.setTextColor(Color.BLACK);
                slotButton.setEnabled(true);

                slotButton.setOnClickListener(v -> {
                    if (selectedSlotButton != null) {
                        selectedSlotButton.setBackgroundResource(R.drawable.slot_button_background);
                        selectedSlotButton.setTextColor(Color.BLACK);
                    }
                    slotButton.setBackgroundResource(R.drawable.slot_button_selected_background);
                    slotButton.setTextColor(Color.WHITE);
                    selectedSlotButton = slotButton;
                    selectedSlot = slot;

                    // Combine selected date with slot time
                    if (selectedSlot != null && selectedSlot.getStartTime() != null) {
                        try {
                            String[] timeParts = selectedSlot.getStartTime().split(":");
                            int hour = Integer.parseInt(timeParts[0]);
                            int minute = Integer.parseInt(timeParts[1]);
                            selectedCalendar.set(Calendar.HOUR_OF_DAY, hour);
                            selectedCalendar.set(Calendar.MINUTE, minute);
                            selectedCalendar.set(Calendar.SECOND, 0);
                            selectedCalendar.set(Calendar.MILLISECOND, 0);
                        } catch (Exception e) {
                            Log.e("CreateBookingActivity", "Could not parse slot time", e);
                        }
                    }
                });
            } else {
                // Slot inactive - màu xám, không thể chọn
                slotButton.setBackgroundResource(R.drawable.slot_button_inactive_background);
                slotButton.setTextColor(Color.BLACK);
                slotButton.setEnabled(false);
            }

            timeSlotGrid.addView(slotButton);
        }
    }

    // Helper method để format thời gian từ HH:mm:ss thành HH:mm
    private String formatTimeToHHMM(String time) {
        if (time == null || time.isEmpty()) {
            return "";
        }

        // Nếu thời gian có format HH:mm:ss, chỉ lấy HH:mm
        if (time.length() >= 5) {
            return time.substring(0, 5);
        }

        return time;
    }

    private void updateServicesUI(List<ServicePackage> servicePackages) {
        radioGroupServices.removeAllViews();
        for (ServicePackage service : servicePackages) {
            RadioButton radioButton = new RadioButton(this);
            String serviceText = service.getName() + " - " + String.format(Locale.GERMAN, "%,.0f", service.getPrice()) + " VND";
            radioButton.setText(serviceText);
            radioButton.setId(View.generateViewId());
            radioGroupServices.addView(radioButton);
        }
    }

    private void updateCartBadge() {
        String userId = SharedPreferencesManager.getInstance(this).getUserId();
        String token = SharedPreferencesManager.getInstance(this).getToken();

        if (userId == null || token == null) {
            cartBadge.setVisibility(View.GONE);
            return;
        }

        ApiService apiService = ApiClient.getAuthenticatedClient(token).create(ApiService.class);
        apiService.getBookings(1, 100, null, userId, "Pending").enqueue(new Callback<WrapResponse<PagingResponse<BookingHistoryResponse>>>() {
            @Override
            public void onResponse(Call<WrapResponse<PagingResponse<BookingHistoryResponse>>> call,
                                   Response<WrapResponse<PagingResponse<BookingHistoryResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int count = response.body().getData().getItems().size();
                    Log.d("CartBadge", "Pending count = " + count);
                    if (count > 0) {
                        cartBadge.setText(String.valueOf(count));
                        cartBadge.setVisibility(View.VISIBLE);
                    } else {
                        cartBadge.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<WrapResponse<PagingResponse<BookingHistoryResponse>>> call, Throwable t) {
                cartBadge.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
    }
}