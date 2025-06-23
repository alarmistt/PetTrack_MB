package com.example.pet_track.ui.register;

import android.content.Intent;
import android.os.Bundle;
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

        btnSignUp.setOnClickListener(v -> {
            String fullName = edtFullName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String address = edtAddress.getText().toString().trim();
            String phone = edtPhone.getText().toString().trim();
            String password = edtPassword.getText().toString();
            String confirmPassword = edtConfirmPassword.getText().toString();

            // Simple validation
            if (fullName.isEmpty() || email.isEmpty() || address.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            RegisterRequest request = new RegisterRequest(fullName, email, password, confirmPassword, address, phone);
            registerViewModel.register(this, request);
        });

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        registerViewModel.getRegisterResult().observe(this, registerResponse -> {
            Toast.makeText(this, "Registration successful! ID: " + registerResponse.getId(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        registerViewModel.getErrorMessage().observe(this, error -> {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        });
    }
}