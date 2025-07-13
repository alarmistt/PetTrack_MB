package com.example.pet_track.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.pet_track.models.response.ServicePackage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartStorageHelper {
    private static final String PREF_NAME = "CartPrefs";
    private static final String KEY_CART = "cart_items";

    public static void saveCart(Context context, String userId, List<ServicePackage> cart) {
        SharedPreferences prefs = context.getSharedPreferences("CartStorage", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        prefs.edit().putString("cart_" + userId, gson.toJson(cart)).apply();
    }

    public static List<ServicePackage> getCart(Context context, String userId) {
        SharedPreferences prefs = context.getSharedPreferences("CartStorage", Context.MODE_PRIVATE);
        String cartJson = prefs.getString("cart_" + userId, null);
        if (cartJson != null) {
            Type type = new TypeToken<List<ServicePackage>>() {}.getType();
            return new Gson().fromJson(cartJson, type);
        }
        return new ArrayList<>();
    }

    public static void clearCart(Context context, String userId) {
        SharedPreferences prefs = context.getSharedPreferences("CartStorage", Context.MODE_PRIVATE);
        prefs.edit().remove("cart_" + userId).apply();
    }
}
