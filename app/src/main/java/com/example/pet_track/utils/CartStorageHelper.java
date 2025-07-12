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

    public static void saveCart(Context context, List<ServicePackage> cartItems) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(cartItems);
        editor.putString(KEY_CART, json);
        editor.apply();
    }

    public static List<ServicePackage> getCart(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_CART, null);

        if (json != null) {
            Type type = new TypeToken<List<ServicePackage>>() {}.getType();
            return new Gson().fromJson(json, type);
        }
        return new ArrayList<>();
    }

    public static void clearCart(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_CART).apply();
    }
}
