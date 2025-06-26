package com.example.pet_track.ui.profile;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.pet_track.R;
import com.example.pet_track.api.ApiClient;
import com.example.pet_track.api.ApiService;
import com.example.pet_track.models.response.UserResponse;
import com.example.pet_track.models.response.WrapResponse;
import com.example.pet_track.utils.SharedPreferencesManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private EditText nameEdit, phoneEdit, addressEdit;
    private ImageView avatarImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actitivy_profile); // sửa lại tên layout chính xác

        // Ánh xạ view từ XML
        nameEdit = findViewById(R.id.nameEdit);
        phoneEdit = findViewById(R.id.phoneEdit);
        addressEdit = findViewById(R.id.addressEdit);
        avatarImage = findViewById(R.id.avatarImage);

        // Lấy token từ SharedPreferences
        String token = SharedPreferencesManager.getInstance(this).getToken();

        // Gọi API lấy thông tin cá nhân
        ApiService apiService = ApiClient.getAuthenticatedClient(token).create(ApiService.class);
        Call<WrapResponse<UserResponse>> call = apiService.getProfile();

        call.enqueue(new Callback<WrapResponse<UserResponse>>() {
            @Override
            public void onResponse(Call<WrapResponse<UserResponse>> call, Response<WrapResponse<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse user = response.body().getData();

                    nameEdit.setText(user.getFullName());
                    phoneEdit.setText(user.getPhoneNumber());
                    addressEdit.setText(user.getAddress());

                    if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                        Glide.with(ProfileActivity.this)
                                .load(user.getAvatarUrl())
                                .into(avatarImage);
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Lỗi khi lấy profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WrapResponse<UserResponse>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
