package com.example.pet_track.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.pet_track.viewmodel.RegisterViewModel;
import com.example.pet_track.R;
import com.example.pet_track.models.request.RegisterRequest;
import com.example.pet_track.ui.login.LoginActivity;

public class RegisterActivity extends AppCompatActivity {

    private RegisterViewModel registerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        EditText edtFullName = findViewById(R.id.editTextText);
        EditText edtEmail = findViewById(R.id.editTextTextEmailAddress);
        EditText edtAddress = findViewById(R.id.editTextAddress);
        EditText edtPhone = findViewById(R.id.editTextPhone);
        EditText edtPassword = findViewById(R.id.editTextTextPassword);
        EditText edtConfirmPassword = findViewById(R.id.editTextTextPassword2);
        Button btnSignUp = findViewById(R.id.button);
        TextView tvLogin = findViewById(R.id.textView10);
        TextView errorMessageTextView = findViewById(R.id.errorMessageTextView);

        btnSignUp.setOnClickListener(v -> {
            errorMessageTextView.setVisibility(View.GONE);

            String fullName = edtFullName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String password = edtPassword.getText().toString();
            String confirmPassword = edtConfirmPassword.getText().toString();

            // Validate form
            if (fullName.isEmpty()) {
                showError(errorMessageTextView, "Vui lòng nhập họ tên");
                return;
            }
            if (email.isEmpty()) {
                showError(errorMessageTextView, "Vui lòng nhập email");
                return;
            }
            if (address.isEmpty()) {
                showError(errorMessageTextView, "Vui lòng nhập địa chỉ");
                return;
            }
            if (phone.isEmpty()) {
                showError(errorMessageTextView, "Vui lòng nhập số điện thoại");
                return;
            }
            if (password.isEmpty()) {
                showError(errorMessageTextView, "Vui lòng nhập mật khẩu");
                return;
            }
            if (confirmPassword.isEmpty()) {
                showError(errorMessageTextView, "Vui lòng xác nhận mật khẩu");
                return;
            }
            if (!password.equals(confirmPassword)) {
                showError(errorMessageTextView, "Mật khẩu không khớp");
                return;
            }

            // Nếu hợp lệ
            RegisterRequest request = new RegisterRequest(fullName, email, password, confirmPassword, address, phone);
            registerViewModel.register(this, request);
        });

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        registerViewModel.getRegisterResult().observe(this, registerResponse -> {
            errorMessageTextView.setVisibility(View.GONE);
            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        registerViewModel.getErrorMessage().observe(this, error -> {
            showError(errorMessageTextView, error); // Show lỗi từ API
        });
    }

    private void showError(TextView errorView, String message) {
        errorView.setText(message);
        errorView.setVisibility(View.VISIBLE);
    }
}