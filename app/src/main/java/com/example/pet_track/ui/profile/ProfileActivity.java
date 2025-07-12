package com.example.pet_track.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
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

    private static final int REQUEST_UPDATE = 1001;

    private TextView nameEdit, phoneEdit, addressEdit;
    private ImageView avatarImage;
    private Button btnUpdateProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actitivy_profile);

        // Ánh xạ view
        nameEdit = findViewById(R.id.nameEdit);
        phoneEdit = findViewById(R.id.phoneEdit);
        addressEdit = findViewById(R.id.addressEdit);
        avatarImage = findViewById(R.id.avatarImage);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);

        btnUpdateProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, UpdateProfileActivity.class);
            intent.putExtra("fullName", nameEdit.getText().toString().trim());
            intent.putExtra("phone", phoneEdit.getText().toString().trim());
            intent.putExtra("address", addressEdit.getText().toString().trim());
            startActivityForResult(intent, REQUEST_UPDATE);
        });

        loadProfile();
    }

    private void loadProfile() {
        String token = SharedPreferencesManager.getInstance(this).getToken();
        ApiService apiService = ApiClient.getAuthenticatedClient(token).create(ApiService.class);

        apiService.getProfile().enqueue(new Callback<WrapResponse<UserResponse>>() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_UPDATE && resultCode == RESULT_OK) {
            loadProfile(); // reload profile sau khi cập nhật thành công
        }
    }
}
