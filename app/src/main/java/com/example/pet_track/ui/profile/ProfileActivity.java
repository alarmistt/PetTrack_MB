package com.example.pet_track.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
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
    private Button btnShowLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actitivy_profile); // đảm bảo đúng tên layout

        // Ánh xạ view từ XML
        nameEdit = findViewById(R.id.nameEdit);
        phoneEdit = findViewById(R.id.phoneEdit);
        addressEdit = findViewById(R.id.addressEdit);
        avatarImage = findViewById(R.id.avatarImage);
        btnShowLocation = findViewById(R.id.btnShowLocation);

        // Xử lý mở Google Maps khi nhấn nút
        btnShowLocation.setOnClickListener(v -> {
            String address = addressEdit.getText().toString().trim();
            if (!address.isEmpty()) {
                try {
                    String encodedAddress = Uri.encode(address);
                    String mapUri = "geo:0,0?q=" + encodedAddress;

                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapUri));

                    // Sử dụng Intent Chooser để hiển thị tất cả ứng dụng có thể xử lý
                    Intent chooser = Intent.createChooser(mapIntent, "Chọn ứng dụng bản đồ");

                    if (chooser.resolveActivity(getPackageManager()) != null) {
                        startActivity(chooser);
                    } else {
                        Toast.makeText(ProfileActivity.this, "Không tìm thấy ứng dụng bản đồ", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Toast.makeText(ProfileActivity.this, "Lỗi khi mở bản đồ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ProfileActivity.this, "Địa chỉ không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });


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
