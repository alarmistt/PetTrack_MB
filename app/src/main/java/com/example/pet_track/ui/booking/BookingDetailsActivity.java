package com.example.pet_track.ui.booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pet_track.R;
import android.util.Log;

public class BookingDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_details_main);
        Log.d("BookingDetailsActivity", "Activity onCreate: layout set.");

        Button btnBook = findViewById(R.id.btnBookAppointment);

        if (btnBook == null) {
            Log.e("BookingDetailsActivity", "LỖI NGHIÊM TRỌNG: Không tìm thấy btnBookAppointment trong layout!");
            return;
        }

        Log.d("BookingDetailsActivity", "Button đã được tìm thấy. Đang gán OnClickListener.");
        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BookingDetailsActivity", "btnBookAppointment clicked");
                Intent intent = new Intent(BookingDetailsActivity.this, com.example.pet_track.ui.booking.BookingPage.class);
                startActivity(intent);
            }
        });
    }
} 