package com.example.pet_track.ui.booking;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.pet_track.R;
import com.example.pet_track.models.response.ServicePackage;
import com.example.pet_track.models.response.Slot;
import com.example.pet_track.ui.cart.CartActivity;
import com.example.pet_track.utils.CartStorageHelper;
import com.example.pet_track.utils.SharedPreferencesManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreateBookingActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private LinearLayout serviceListContainer;
    private GridLayout timeSlotGrid;
    private TextView dateTextView;
    private Button btnXacNhan;
    private BookingViewModel bookingViewModel;
    private String clinicId;
    private Button selectedSlotButton = null;
    private Slot selectedSlot = null;
    private List<ServicePackage> servicePackages = new ArrayList<>();
    private List<ServicePackage> selectedPackages = new ArrayList<>();
    private Calendar selectedCalendar = Calendar.getInstance();
    private ImageView cartIcon;
    private TextView cartBadge;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_booking);

        cartIcon = findViewById(R.id.cart_icon);
        cartBadge = findViewById(R.id.cart_badge);

        userId = SharedPreferencesManager.getInstance(this).getUserId();

        clinicId = getIntent().getStringExtra("clinicId");
        if (clinicId == null || clinicId.isEmpty()) {
            Toast.makeText(this, "Clinic ID is missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // ✅ Phải kiểm tra và reset cart TRƯỚC KHI gọi getCart
        String lastClinicId = SharedPreferencesManager.getInstance(this).getLastClinicId();
        if (lastClinicId != null && !lastClinicId.equals(clinicId)) {
            CartStorageHelper.clearCart(this, userId); // clear cart
            selectedPackages.clear();                  // clear RAM
        }
        SharedPreferencesManager.getInstance(this).setLastClinicId(clinicId);

        // ✅ Sau khi xử lý xong, mới get lại cart (sẽ là rỗng nếu vừa clear)
        selectedPackages = CartStorageHelper.getCart(this, userId);
        updateCartBadge();

        cartIcon.setOnClickListener(v -> {
            if (selectedPackages.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng đang trống.", Toast.LENGTH_SHORT).show();
            } else if (selectedSlot == null) {
                Toast.makeText(this, "Vui lòng chọn khung giờ trước khi xem giỏ hàng.", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(CreateBookingActivity.this, CartActivity.class);
                intent.putParcelableArrayListExtra("selectedServices", new ArrayList<>(selectedPackages));

                String slotText = formatTimeToHHMM(selectedSlot.getStartTime()) + " - " + formatTimeToHHMM(selectedSlot.getEndTime());
                long selectedTimeMillis = selectedCalendar.getTimeInMillis();

                intent.putExtra("slotText", slotText);
                intent.putExtra("slotDate", selectedTimeMillis);

                startActivity(intent);
            }
        });

        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        calendarView = findViewById(R.id.calendarView);
        serviceListContainer = findViewById(R.id.service_list_container);
        timeSlotGrid = findViewById(R.id.time_slot_grid);
        dateTextView = findViewById(R.id.dateTextView);
        btnXacNhan = findViewById(R.id.btnXacNhan);

        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        setupObservers();
        updateDateDisplay(selectedCalendar);

        fetchSlotsForDate(selectedCalendar);
        bookingViewModel.getClinicDetails(clinicId);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedCalendar.set(year, month, dayOfMonth);
            updateDateDisplay(selectedCalendar);
            fetchSlotsForDate(selectedCalendar);
        });

        btnXacNhan.setOnClickListener(v -> {
            if (selectedPackages.isEmpty()) {
                Toast.makeText(this, "Chưa chọn gói dịch vụ nào.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(CreateBookingActivity.this, CartActivity.class);
            intent.putParcelableArrayListExtra("selectedServices", new ArrayList<>(selectedPackages));

            String slotText = formatTimeToHHMM(selectedSlot.getStartTime()) + " - " + formatTimeToHHMM(selectedSlot.getEndTime());
            long selectedTimeMillis = selectedCalendar.getTimeInMillis();

            intent.putExtra("slotText", slotText);
            intent.putExtra("slotDate", selectedTimeMillis);

            startActivity(intent);
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

            boolean isActive = slot.getStatus() != null && slot.getStatus().equalsIgnoreCase("Active");

            if (isActive) {
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
                slotButton.setBackgroundResource(R.drawable.slot_button_inactive_background);
                slotButton.setTextColor(Color.BLACK);
                slotButton.setEnabled(false);
            }

            timeSlotGrid.addView(slotButton);
        }
    }

    private String formatTimeToHHMM(String time) {
        if (time == null || time.isEmpty()) {
            return "";
        }
        if (time.length() >= 5) {
            return time.substring(0, 5);
        }
        return time;
    }

    private void updateServicesUI(List<ServicePackage> servicePackages) {
        serviceListContainer.removeAllViews(); // xoá UI cũ

        for (ServicePackage service : servicePackages) {
            CheckBox checkBox = new CheckBox(this);
            String serviceText = service.getName() + " - " + String.format(Locale.GERMANY, "%,.0f", service.getPrice()) + " VND";
            checkBox.setText(serviceText);
            checkBox.setTextColor(Color.BLACK);

            // Nếu gói này đã có trong cart → tick lại
            boolean isChecked = false;
            for (ServicePackage selected : selectedPackages) {
                if (selected.getId().equals(service.getId())) {
                    isChecked = true;
                    break;
                }
            }
            checkBox.setChecked(isChecked);

            checkBox.setOnCheckedChangeListener((buttonView, isChecked1) -> {
                if (isChecked1) {
                    selectedPackages.add(service);
                } else {
                    selectedPackages.removeIf(p -> p.getId().equals(service.getId()));
                }
                updateCartBadge();
                CartStorageHelper.saveCart(this, userId, selectedPackages);
            });

            serviceListContainer.addView(checkBox);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        selectedPackages.clear();
        selectedPackages = CartStorageHelper.getCart(this, userId);
        updateCartBadge();
        updateServicesUI(this.servicePackages);
    }

    private void updateCartBadge() {
        int count = selectedPackages.size();
        if (count > 0) {
            cartBadge.setVisibility(View.VISIBLE);
            cartBadge.setText(String.valueOf(count));
        } else {
            cartBadge.setVisibility(View.GONE);
        }
    }
}