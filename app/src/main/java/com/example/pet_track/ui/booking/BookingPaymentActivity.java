package com.example.pet_track.ui.booking;

import static android.content.Intent.getIntent;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pet_track.R;
import com.example.pet_track.ui.wallet.BookingPaymentFragment;

public class BookingPaymentActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_payment); // chứa FrameLayout

        String bookingId = getIntent().getStringExtra("booking_id");
        int amount = getIntent().getIntExtra("amount", 0);

        BookingPaymentFragment fragment = BookingPaymentFragment.newInstance(bookingId, amount);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.payment_fragment_container, fragment)
                .commit();
    }
}
