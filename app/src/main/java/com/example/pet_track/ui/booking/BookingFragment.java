package com.example.pet_track.ui.booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.pet_track.R;
import com.example.pet_track.databinding.FragmentBookingBinding;

public class BookingFragment extends Fragment {

    private FragmentBookingBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        BookingViewModel bookingViewModel =
                new ViewModelProvider(this).get(BookingViewModel.class);

        binding = FragmentBookingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Hiển thị phần booking (header, content, footer)
        LinearLayout contentContainer = binding.bookingContentContainer;
        contentContainer.removeAllViews();
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View headerView = layoutInflater.inflate(R.layout.header_booking, contentContainer, false);
        View contentView = layoutInflater.inflate(R.layout.content, contentContainer, false);
        View footerView = layoutInflater.inflate(R.layout.footer, contentContainer, false);
        contentContainer.addView(headerView);
        contentContainer.addView(contentView);
        contentContainer.addView(footerView);

        // Gán sự kiện cho tất cả các nút Đặt lịch
        View.OnClickListener bookingClickListener = v -> {
            Intent intent = new Intent(getActivity(), BookingDetailsActivity.class);
            startActivity(intent);
        };

        contentView.findViewById(R.id.btn_booking_1).setOnClickListener(bookingClickListener);
        contentView.findViewById(R.id.btn_booking_2).setOnClickListener(bookingClickListener);
        contentView.findViewById(R.id.btn_booking_3).setOnClickListener(bookingClickListener);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 