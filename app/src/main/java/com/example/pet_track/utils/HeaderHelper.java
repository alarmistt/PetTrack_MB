package com.example.pet_track.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pet_track.R;
import com.example.pet_track.api.ApiClient;
import com.example.pet_track.api.ApiService;
import com.example.pet_track.models.response.BookingHistoryResponse;
import com.example.pet_track.models.response.PagingResponse;
import com.example.pet_track.models.response.WrapResponse;
import com.example.pet_track.ui.cart.CartActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HeaderHelper {

    public static void setupCartHeader(View rootView, Context context) {
        ImageView btnCart = rootView.findViewById(R.id.icon_cart);
        TextView cartBadge = rootView.findViewById(R.id.cart_badge);

        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(context, CartActivity.class);
            context.startActivity(intent);
        });

        loadCartItemCount(cartBadge, context);
    }

    private static void loadCartItemCount(TextView cartBadge, Context context) {
        String userId = SharedPreferencesManager.getInstance(context).getUserId();
        String token = SharedPreferencesManager.getInstance(context).getToken();

        if (userId == null || token == null || token.isEmpty()) {
            cartBadge.setVisibility(View.GONE);
            return;
        }

        ApiService apiService = ApiClient.getAuthenticatedClient(token).create(ApiService.class);
        apiService.getBookings(1, 50, null, userId, "Pending")
                .enqueue(new Callback<WrapResponse<PagingResponse<BookingHistoryResponse>>>() {
                    @Override
                    public void onResponse(Call<WrapResponse<PagingResponse<BookingHistoryResponse>>> call, Response<WrapResponse<PagingResponse<BookingHistoryResponse>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            int count = response.body().getData().getItems().size();
                            if (count > 0) {
                                cartBadge.setText(String.valueOf(count));
                                cartBadge.setVisibility(View.VISIBLE);
                            } else {
                                cartBadge.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<WrapResponse<PagingResponse<BookingHistoryResponse>>> call, Throwable t) {
                        Log.e("HeaderHelper", "Failed to load cart count", t);
                        cartBadge.setVisibility(View.GONE);
                    }
                });
    }

    public static void updateCartBadge(TextView cartBadge, Context context) {
        String userId = SharedPreferencesManager.getInstance(context).getUserId();
        String token = SharedPreferencesManager.getInstance(context).getToken();

        if (userId == null || token == null) {
            cartBadge.setVisibility(View.GONE);
            return;
        }

        ApiService apiService = ApiClient.getAuthenticatedClient(token).create(ApiService.class);
        apiService.getBookings(1, 100, null, userId, "Pending")
                .enqueue(new Callback<WrapResponse<PagingResponse<BookingHistoryResponse>>>() {
                    @Override
                    public void onResponse(Call<WrapResponse<PagingResponse<BookingHistoryResponse>>> call,
                                           Response<WrapResponse<PagingResponse<BookingHistoryResponse>>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            int count = response.body().getData().getItems().size();
                            Log.d("CartBadge", "Pending count = " + count);
                            if (count > 0) {
                                cartBadge.setText(String.valueOf(count));
                                cartBadge.setVisibility(View.VISIBLE);
                            } else {
                                cartBadge.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<WrapResponse<PagingResponse<BookingHistoryResponse>>> call, Throwable t) {
                        cartBadge.setVisibility(View.GONE);
                    }
                });
    }

}
