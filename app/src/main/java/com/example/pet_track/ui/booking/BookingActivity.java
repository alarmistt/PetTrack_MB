package com.example.pet_track.ui.booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pet_track.R;

public class BookingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);


        ImageView btnProfile = findViewById(R.id.icon_profile);
        ImageView btnHome = findViewById(R.id.icon_home);
        ImageView btnCalendar = findViewById(R.id.icon_calendar);
        ImageView btnChat = findViewById(R.id.icon_chat);

        Button btnBooking1 = findViewById(R.id.btn_booking_1);
        Button btnBooking2 = findViewById(R.id.btn_booking_2);
        Button btnBooking3 = findViewById(R.id.btn_booking_3);

        View.OnClickListener bookingClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookingActivity.this, BookingDetailsActivity.class);
                startActivity(intent);
            }
        };
        btnBooking1.setOnClickListener(bookingClickListener);
        btnBooking2.setOnClickListener(bookingClickListener);
        btnBooking3.setOnClickListener(bookingClickListener);

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logic cho btn_home
            }
        });


        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logic cho btn_calendar, ví dụ: mở lịch hoặc fragment
                // Intent intent = new Intent(BookingActivity.this, CalendarActivity.class);
                // startActivity(intent);
            }
        });


        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logic cho btn_chat
            }
        });
    }
}