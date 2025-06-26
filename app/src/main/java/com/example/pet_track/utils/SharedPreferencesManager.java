package com.example.pet_track.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.pet_track.models.response.UserResponse;

public class SharedPreferencesManager {
    private static final String PREF_NAME = "PetTrackPrefs";
    private static final String KEY_TOKEN = "token";
    private static SharedPreferencesManager instance;
    private final SharedPreferences preferences;

    private SharedPreferencesManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPreferencesManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesManager(context.getApplicationContext());
        }
        return instance;
    }
    public void putString(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }

    public String getString(String key, String defaultValue) {
        return preferences.getString(key, defaultValue);
    }
    public void saveToken(String token) {
        preferences.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return preferences.getString(KEY_TOKEN, null);
    }

    public void clear() {
        preferences.edit().clear().apply();
    }

    public void saveUserInfo(UserResponse user) {
        preferences.edit()
                .putString("userId", user.getId())
                .putString("email", user.getEmail())
                .putString("fullName", user.getFullName())
                .putString("phoneNumber", user.getPhoneNumber())
                .putString("role", user.getRole())
                .putString("avatarUrl", user.getAvatarUrl())
                .apply();
    }

    public UserResponse getUserInfo() {
        return new UserResponse(
                getUserId(),
                getEmail(),
                getFullName(),
                getPhoneNumber(),
                getRole(),
                getAvatarUrl()
        );
    }

    public boolean isLoggedIn() {
        return getToken() != null && getUserId() != null;
    }

    public String getUserId() {
        return preferences.getString("userId", null);
    }

    public String getFullName() {
        return preferences.getString("fullName", null);
    }

    public String getEmail() {
        return preferences.getString("email", null);
    }

    public String getPhoneNumber() {
        return preferences.getString("phoneNumber", null);
    }

    public String getRole() {
        return preferences.getString("role", null);
    }

    public String getAvatarUrl() {
        return preferences.getString("avatarUrl", null);
    }
}