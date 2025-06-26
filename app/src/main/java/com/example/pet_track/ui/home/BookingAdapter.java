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

        // Tên phòng khám
        holder.tvClinicName.setText(item.getClinicName());

        // Tên dịch vụ
        holder.tvServiceName.setText("💼 Dịch vụ: " + item.getServicePackageName());

        // Ngày hẹn (cắt chuỗi yyyy-MM-dd)
        String date = item.getAppointmentDate();
        holder.tvDate.setText("📅 " + (date.length() >= 10 ? date.substring(0, 10) : date));

        // Giá định dạng có dấu chấm
        String formattedPrice = String.format("%,d", item.getPrice()).replace(",", ".");
        holder.tvPrice.setText("💰 " + formattedPrice + " VND");

        // Trạng thái có emoji và đổi màu
        String status = item.getStatus();
        if (status.equalsIgnoreCase("Pending")) {
            holder.tvStatus.setText("🟡 Đang chờ");
            holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_orange_dark));
        } else if (status.equalsIgnoreCase("Confirmed")) {
            holder.tvStatus.setText("🟢 Đã xác nhận");
            holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
        } else if (status.equalsIgnoreCase("Cancelled")) {
            holder.tvStatus.setText("🔴 Đã hủy");
            holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        } else {
            holder.tvStatus.setText("⚪ " + status);
            holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray));
        }
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