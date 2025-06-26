package com.example.pet_track.ui.profile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pet_track.R;
import com.example.pet_track.api.ApiClient;
import com.example.pet_track.api.ApiService;

import com.example.pet_track.models.response.UserUpdateRequest;
import com.example.pet_track.utils.SharedPreferencesManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText nameEdit, phoneEdit, addressEdit;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actitivy_profile); // chắc chắn tên file XML đúng

        // Ánh xạ view
        nameEdit = findViewById(R.id.nameEdit);
        phoneEdit = findViewById(R.id.phoneEdit);
        addressEdit = findViewById(R.id.addressEdit);
        saveButton = findViewById(R.id.btnSave); // Đảm bảo bạn có android:id="@+id/btnSave" trong XML

        // Bắt sự kiện nút Save
        saveButton.setOnClickListener(v -> {
            String fullName = nameEdit.getText().toString().trim();
            String phoneNumber = phoneEdit.getText().toString().trim();
            String address = addressEdit.getText().toString().trim();
            String avatarUrl = ""; // nếu bạn chưa cho người dùng chọn ảnh thì để tạm trống

            UserUpdateRequest request = new UserUpdateRequest(fullName, address, phoneNumber, avatarUrl);

            String token = SharedPreferencesManager.getInstance(this).getToken();
            ApiService apiService = ApiClient.getAuthenticatedClient(token).create(ApiService.class);

            apiService.updateProfile(request).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(UpdateProfileActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UpdateProfileActivity.this, "Lỗi khi cập nhật", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(UpdateProfileActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
