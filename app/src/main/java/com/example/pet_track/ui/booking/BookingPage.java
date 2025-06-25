package com.example.pet_track.ui.booking;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pet_track.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BookingPage extends AppCompatActivity {

    private CalendarView calendarView;
    private RadioGroup radioGroup;
    private EditText editNote;
    private TextView dateTextView;
    private Button btnXacNhan; // Assuming Button is defined in XML

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_page);

        calendarView = findViewById(R.id.calendarView);
        radioGroup = findViewById(R.id.radioGroup);
        editNote = findViewById(R.id.editNote);
        dateTextView = findViewById(R.id.dateTextView); // Add this ID to your TextView in XML
        btnXacNhan = findViewById(R.id.btnXacNhan);

        // Set initial date and day of the week
        updateDateDisplay();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM", new Locale("vi"));
                String date = sdf.format(calendar.getTime());
                dateTextView.setText(date);
            }
        });

        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = findViewById(selectedId);
                String service = radioButton.getText().toString();
                String note = editNote.getText().toString();
                Toast.makeText(BookingPage.this, "Đã chọn: " + service + "\nGhi chú: " + note, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateDateDisplay() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd 'tháng' MM", new Locale("vi"));
        String currentDate = sdf.format(calendar.getTime());
        dateTextView.setText(currentDate);
    }
}