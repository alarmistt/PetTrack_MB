package com.example.pet_track.ui.booking;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;
import androidx.lifecycle.ViewModelProvider;

import com.example.pet_track.R;
import com.example.pet_track.models.response.ServicePackage;
import com.example.pet_track.models.response.Slot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CreateBookingActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private RadioGroup radioGroupServices;
    private GridLayout timeSlotGrid;
    private EditText editNote;
    private TextView dateTextView;
    private Button btnXacNhan;
    private BookingViewModel bookingViewModel;
    private String clinicId;
    private Button selectedSlotButton = null;
    private Slot selectedSlot = null;
    private List<ServicePackage> servicePackages = new ArrayList<>();
    private Calendar selectedCalendar = Calendar.getInstance();

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

        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        calendarView = findViewById(R.id.calendarView);
        radioGroupServices = findViewById(R.id.radioGroup);
        timeSlotGrid = findViewById(R.id.time_slot_grid);
        editNote = findViewById(R.id.editNote);
        dateTextView = findViewById(R.id.dateTextView);
        btnXacNhan = findViewById(R.id.btnXacNhan);

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

            String note = editNote.getText().toString();

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
            String slotText = slot.getStartTime() + " - " + slot.getEndTime();
            slotButton.setText(slotText);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(8, 8, 8, 8);
            slotButton.setLayoutParams(params);
            slotButton.setBackgroundColor(Color.LTGRAY);

            slotButton.setOnClickListener(v -> {
                if (selectedSlotButton != null) {
                    selectedSlotButton.setBackgroundColor(Color.LTGRAY);
                }
                slotButton.setBackgroundColor(Color.CYAN);
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
            timeSlotGrid.addView(slotButton);
        }
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
} 