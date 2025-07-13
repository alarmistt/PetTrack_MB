package com.example.pet_track.ui.wallet;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pet_track.R;
import com.example.pet_track.models.response.wallet.TopUpResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TopUpHistoryAdapter extends RecyclerView.Adapter<TopUpHistoryAdapter.TopUpViewHolder> {
    private final List<TopUpResponse> topUps;

    public TopUpHistoryAdapter(List<TopUpResponse> topUps) {
        this.topUps = topUps;
    }

    @NonNull
    @Override
    public TopUpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topup, parent, false);
        return new TopUpViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopUpViewHolder holder, int position) {
        TopUpResponse item = topUps.get(position);
        holder.tvAmount.setText(String.format("%,.0f VNĐ", item.getAmount()));
        holder.tvCreatedTime.setText(formatDate(item.getCreatedTime()));
        holder.tvStatus.setText(item.getStatus());

        // Trạng thái màu
        String status = item.getStatus().toLowerCase();
        if (status.contains("paid") || status.contains("success")) {
            holder.tvStatus.setTextColor(Color.parseColor("#2E7D32")); // xanh lá
        } else {
            holder.tvStatus.setTextColor(Color.parseColor("#C62828")); // đỏ
        }
    }


    @Override
    public int getItemCount() {
        return topUps.size();
    }

    public static class TopUpViewHolder extends RecyclerView.ViewHolder {
        TextView tvAmount, tvCreatedTime, tvStatus;

        public TopUpViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvCreatedTime = itemView.findViewById(R.id.tvCreatedTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }

    private String formatDate(String isoDateTime) {
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            Date date = isoFormat.parse(isoDateTime);
            SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return displayFormat.format(date);
        } catch (Exception e) {
            return isoDateTime;
        }
    }
}
