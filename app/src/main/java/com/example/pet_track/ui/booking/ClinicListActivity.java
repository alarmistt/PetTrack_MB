package com.example.pet_track.ui.booking;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.pet_track.R;

public class ClinicListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Thêm button back
        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish(); // Quay về activity trước
                }
            });
        }

        if (savedInstanceState == null) {
            Fragment fragment = new ClinicListFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.booking_content_container, fragment);
            transaction.commit();
        }

        ImageView btnProfile = findViewById(R.id.icon_profile);
        ImageView btnHome = findViewById(R.id.icon_home);
        ImageView btnCalendar = findViewById(R.id.icon_calendar);
        ImageView btnChat = findViewById(R.id.icon_chat);

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
                // Intent intent = new Intent(ClinicListActivity.this, CalendarActivity.class);
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