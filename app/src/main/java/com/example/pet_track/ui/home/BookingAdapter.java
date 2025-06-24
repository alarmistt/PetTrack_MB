package com.example.pet_track.ui.home;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pet_track.R;
import com.example.pet_track.models.response.BookingHistoryResponse;

import java.util.ArrayList;
import java.util.List;
public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<BookingHistoryResponse> bookingList = new ArrayList<>();

    public void updateData(List<BookingHistoryResponse> list) {
        if (list == null) {
            bookingList = new ArrayList<>();
        } else {
            bookingList = list;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        BookingHistoryResponse item = bookingList.get(position);
        holder.tvClinicName.setText(item.getClinicName());
        holder.tvServiceName.setText(item.getServicePackageName());
        holder.tvStatus.setText(item.getStatus());
        holder.tvDate.setText(item.getAppointmentDate().substring(0, 10)); // yyyy-MM-dd
        holder.tvPrice.setText(item.getPrice() + " VND");
    }

    @Override
    public int getItemCount() {
        return bookingList != null ? bookingList.size() : 0;
    }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvClinicName, tvServiceName, tvStatus, tvDate, tvPrice;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClinicName = itemView.findViewById(R.id.tvClinicName);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}