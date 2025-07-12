package com.example.pet_track.ui.cart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import com.example.pet_track.R;
import com.example.pet_track.models.response.ServicePackage;
import com.example.pet_track.ui.booking.PaymentBookingActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;


public class CartActivity extends AppCompatActivity {

    private LinearLayout cartItemsContainer;
    private TextView totalPriceText;
    private Button btnCheckout;
    private double totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        cartItemsContainer = findViewById(R.id.cart_items_container);
        totalPriceText = findViewById(R.id.total_price);
        btnCheckout = findViewById(R.id.btn_checkout);

        // Lấy dữ liệu giả lập từ Intent
        ArrayList<ServicePackage> cartItems = getIntent().getParcelableArrayListExtra("selectedServices");
        String slotText = getIntent().getStringExtra("slotText");
        long slotDateMillis = getIntent().getLongExtra("slotDate", -1);

        String formattedDate = "";
        if (slotDateMillis != -1) {
            formattedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(slotDateMillis);
        }

        if (cartItems != null) {
            for (ServicePackage item : new ArrayList<>(cartItems)) {
                addCartItemView(item, formattedDate, slotText, cartItems);
                totalPrice += item.getPrice();
            }
            updateTotalPrice();
        }

        btnCheckout.setOnClickListener(v -> {
            if (!cartItems.isEmpty()) {
                // Mô phỏng đi tiếp đến màn hình thanh toán
                ServicePackage firstItem = cartItems.get(0); // chỉ lấy 1 cái
                Intent intent = new Intent(CartActivity.this, PaymentBookingActivity.class);
                intent.putExtra("serviceName", firstItem.getName());
                intent.putExtra("servicePrice", firstItem.getPrice());
                intent.putExtra("servicePackageId", firstItem.getId());
                intent.putExtra("note", "Sản phẩm từ giỏ hàng");
                intent.putExtra("date", System.currentTimeMillis());
                intent.putExtra("slotText", "09:00 - 10:00");
                intent.putExtra("slotId", "mock-slot-123");
                startActivity(intent);
            }
        });
    }

    private void addCartItemView(ServicePackage item, String date, String time, ArrayList<ServicePackage> cartItems) {
        LinearLayout horizontalLayout = new LinearLayout(this);
        horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
        horizontalLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        horizontalLayout.setPadding(0, 12, 0, 12);

        LinearLayout infoLayout = new LinearLayout(this);
        infoLayout.setOrientation(LinearLayout.VERTICAL);
        infoLayout.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView nameText = new TextView(this);
        nameText.setText("• " + item.getName());
        nameText.setTextSize(16);
        infoLayout.addView(nameText);

        TextView priceText = new TextView(this);
        priceText.setText("Giá: " + String.format(Locale.getDefault(), "%,.0f", item.getPrice()) + " VND");
        priceText.setTextSize(14);
        infoLayout.addView(priceText);

        TextView timeText = new TextView(this);
        timeText.setText("Thời gian: " + time + " - " + date);
        timeText.setTextSize(13);
        infoLayout.addView(timeText);

        horizontalLayout.addView(infoLayout);

        // Icon xoá bằng ImageView
        ImageView deleteIcon = new ImageView(this);
        deleteIcon.setImageResource(R.drawable.ic_delete);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(60, 60);
        iconParams.setMargins(16, 0, 0, 0);
        deleteIcon.setLayoutParams(iconParams);

        deleteIcon.setOnClickListener(v -> {
            cartItems.remove(item);
            cartItemsContainer.removeView(horizontalLayout);
            totalPrice -= item.getPrice();
            updateTotalPrice();
        });

        horizontalLayout.addView(deleteIcon);
        cartItemsContainer.addView(horizontalLayout);
    }


    private void updateTotalPrice() {
        totalPriceText.setText("Tổng: " + String.format(Locale.getDefault(), "%,.0f", totalPrice) + " VND");
    }
}
