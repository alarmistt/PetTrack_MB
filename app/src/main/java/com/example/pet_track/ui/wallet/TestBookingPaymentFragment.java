package com.example.pet_track.ui.wallet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pet_track.R;

public class TestBookingPaymentFragment extends Fragment {
    private EditText edtBookingId, edtAmount;
    private Button btnPay;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test_booking_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        edtBookingId = view.findViewById(R.id.edtBookingId);
        edtAmount = view.findViewById(R.id.edtAmount);
        btnPay = view.findViewById(R.id.btnPayBooking);

        btnPay.setOnClickListener(v -> {
            String bookingId = edtBookingId.getText().toString().trim();
            String amountStr = edtAmount.getText().toString().trim();

            if (bookingId.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ", Toast.LENGTH_SHORT).show();
                return;
            }

            int amount;
            try {
                amount = Integer.parseInt(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Số tiền không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            BookingPaymentFragment fragment = BookingPaymentFragment.newInstance(bookingId, amount);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment_content_main, fragment) // đúng ID layout
                    .addToBackStack(null)
                    .commit();
        });
    }
}
