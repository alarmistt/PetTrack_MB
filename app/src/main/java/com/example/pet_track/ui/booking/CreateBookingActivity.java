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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_booking);

        cartIcon = findViewById(R.id.cart_icon);
        cartBadge = findViewById(R.id.cart_badge);

        selectedPackages = CartStorageHelper.getCart(this);
        updateCartBadge();

        cartIcon.setOnClickListener(v -> {
            if (selectedPackages.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng đang trống.", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(CreateBookingActivity.this, CartActivity.class);
                intent.putParcelableArrayListExtra("selectedServices", new ArrayList<>(selectedPackages));
                // Gửi thông tin slot nếu cần
                startActivity(intent);
            }
        });

        // Cập nhật số lượng ban đầu
        updateCartBadge();

        clinicId = getIntent().getStringExtra("clinicId");
        if (clinicId == null || clinicId.isEmpty()) {
            Toast.makeText(this, "Clinic ID is missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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

            // Gửi thêm thông tin slot
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
        List<ServicePackage> savedCart = CartStorageHelper.getCart(this);
        selectedPackages.clear();
        selectedPackages.addAll(savedCart);

        serviceListContainer.removeAllViews(); // chỉ xoá UI, không xoá data

        for (ServicePackage service : servicePackages) {
            CheckBox checkBox = new CheckBox(this);
            String serviceText = service.getName() + " - " + String.format(Locale.GERMANY, "%,.0f", service.getPrice()) + " VND";
            checkBox.setText(serviceText);
            checkBox.setTextColor(Color.BLACK);

            // Nếu gói này đã có trong cart → tick lại
            for (ServicePackage selected : selectedPackages) {
                if (selected.getId().equals(service.getId())) {
                    checkBox.setChecked(true);
                    break;
                }
            }

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    selectedPackages.add(service);
                } else {
                    selectedPackages.removeIf(p -> p.getId().equals(service.getId()));
                }
                updateCartBadge();
                CartStorageHelper.saveCart(this, selectedPackages); // luôn lưu lại sau khi thay đổi
            });

            serviceListContainer.addView(checkBox);
        }
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