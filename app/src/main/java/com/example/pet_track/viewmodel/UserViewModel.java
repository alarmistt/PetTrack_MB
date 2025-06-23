package com.example.pet_track.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.pet_track.utils.SharedPreferencesManager;

public class UserViewModel extends AndroidViewModel {
    private final MutableLiveData<String> fullName = new MutableLiveData<>();
    private final MutableLiveData<String> email = new MutableLiveData<>();
    private final MutableLiveData<String> avatarUrl = new MutableLiveData<>();

    public UserViewModel(@NonNull Application application) {
        super(application);
        loadUserInfo();
    }

    public LiveData<String> getFullName() {
        return fullName;
    }

    public LiveData<String> getEmail() {
        return email;
    }

    public LiveData<String> getAvatarUrl() {
        return avatarUrl;
    }

    public void loadUserInfo() {
        SharedPreferencesManager prefs = SharedPreferencesManager.getInstance(getApplication());
        fullName.setValue(prefs.getFullName());
        email.setValue(prefs.getEmail());
        avatarUrl.setValue(prefs.getAvatarUrl());
    }

    public void clearUserInfo() {
        SharedPreferencesManager.getInstance(getApplication()).clear();
        fullName.setValue("");
        email.setValue("");
        avatarUrl.setValue("");
    }
}

