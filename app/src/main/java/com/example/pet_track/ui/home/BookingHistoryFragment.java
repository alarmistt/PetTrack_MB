package com.example.pet_track.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pet_track.R;

import com.example.pet_track.api.ApiService;
import com.example.pet_track.api.ApiServiceBuilder;
import com.example.pet_track.models.response.BookingHistoryResponse;
import com.example.pet_track.models.response.PagingResponse;
import com.example.pet_track.models.response.WrapResponse;
import com.example.pet_track.utils.SharedPreferencesManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingHistoryFragment extends Fragment {

    private RecyclerView recyclerView;
    private BookingAdapter bookingAdapter;
    private Button btnBooked, btnUpcoming, btnCancelled;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_history, container, false); // ví dụ: fragment_booking_history
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerBooked);
        btnBooked = view.findViewById(R.id.btnBooked);
        btnUpcoming = view.findViewById(R.id.btnUpcoming);
        btnCancelled = view.findViewById(R.id.btnCancelled);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bookingAdapter = new BookingAdapter();
        recyclerView.setAdapter(bookingAdapter);

        // Mặc định gọi Pending và tô màu tab Booked
        fetchBookingHistory("Pending");
        setActiveTab(btnBooked);

        // Tab Booked → trạng thái Pending
        btnBooked.setOnClickListener(v -> {
            fetchBookingHistory("Pending");
            setActiveTab(btnBooked);
        });

        // Tab Upcoming → trạng thái Cancelled
        btnUpcoming.setOnClickListener(v -> {
            fetchBookingHistory("Cancelled");
            setActiveTab(btnUpcoming);
        });

        // Tab Cancelled → trạng thái Completed
        btnCancelled.setOnClickListener(v -> {
            fetchBookingHistory("Completed");
            setActiveTab(btnCancelled);
        });
    }

    private void setActiveTab(Button activeButton) {
        // Reset màu tất cả
        btnBooked.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btnUpcoming.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        btnCancelled.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

        // Màu nổi bật cho tab được chọn
        activeButton.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
    }

    private void fetchBookingHistory(String status) {
        String userId = SharedPreferencesManager.getInstance(getContext()).getUserId();

        ApiService apiService = ApiServiceBuilder.buildService(ApiService.class, getContext());
        apiService.getBookings(1, 10, "", userId, status)
                .enqueue(new Callback<WrapResponse<PagingResponse<BookingHistoryResponse>>>() {
                    @Override
                    public void onResponse(Call<WrapResponse<PagingResponse<BookingHistoryResponse>>> call,
                                           Response<WrapResponse<PagingResponse<BookingHistoryResponse>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            PagingResponse<BookingHistoryResponse> pagingData = response.body().getData();
                            List<BookingHistoryResponse> bookings = (pagingData != null) ? pagingData.getItems() : new ArrayList<>();

                            if (bookings == null) {
                                bookings = new ArrayList<>(); // fallback nếu vẫn null
                            }

                            Log.d("BookingDebug", "Tổng số lịch sử: " + bookings.size());
                            Log.d("BookingDebug", " lịch sử: " + bookings);
                            Log.d("BookingDebug", "userId = " + userId + ", status = " + status);
                            Gson gson = new GsonBuilder().setPrettyPrinting().create();
                            Log.d("BookingDebug", "Full response:\n" + gson.toJson(response.body()));

                            bookingAdapter.updateData(bookings);



                        } else {
                            Toast.makeText(getContext(), "Lỗi khi tải lịch sử", Toast.LENGTH_SHORT).show();
                            Log.e("API_ERROR", "Code: " + response.code() + ", Message: " + response.message());
                        }
                    }



                    @Override
                    public void onFailure(Call<WrapResponse<PagingResponse<BookingHistoryResponse>>> call, Throwable t) {
                        Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("API_FAILURE", "Throwable: ", t);
                    }

                });
    }
}
