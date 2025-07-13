package com.example.pet_track.ui.booking;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.pet_track.R;
import com.example.pet_track.api.ApiService;
import com.example.pet_track.api.ApiServiceBuilder;
import com.example.pet_track.models.response.ClinicResponse;
import com.example.pet_track.models.response.ServicePackage;
import com.example.pet_track.models.response.WrapResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;

public class ClinicDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_details);

        // Thêm button back
        ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        Intent intent = getIntent();
        String clinicId = intent.getStringExtra("clinic_id");
        Log.d("ClinicDetails", "Received clinic_id: " + clinicId);
        if (clinicId == null) {
            Toast.makeText(this, "Không tìm thấy phòng khám!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Gọi API lấy danh sách clinic, tìm clinic theo id
        ApiService api = ApiServiceBuilder.buildService(ApiService.class, this);
        api.getClinicDetails(clinicId).enqueue(new Callback<WrapResponse<ClinicResponse>>() {
            @Override
            public void onResponse(Call<WrapResponse<ClinicResponse>> call, Response<WrapResponse<ClinicResponse>> response) {
                Log.d("ClinicDetails", "API onResponse. Code: " + response.code());
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    ClinicResponse clinic = response.body().getData();
                    Log.d("ClinicDetails", "Clinic found: " + clinic.getName());
                    showClinic(clinic);
                } else {
                    Log.e("ClinicDetails", "API call failed or data is null. Response: " + response.toString());
                    Toast.makeText(ClinicDetailsActivity.this, "Không lấy được dữ liệu phòng khám!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<WrapResponse<ClinicResponse>> call, Throwable t) {
                Log.e("ClinicDetails", "API onFailure: " + t.getMessage(), t);
                Toast.makeText(ClinicDetailsActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        Button btnDatLich = findViewById(R.id.btnBookAppointment);
        if (btnDatLich != null) {
            btnDatLich.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(ClinicDetailsActivity.this, CreateBookingActivity.class);
                    i.putExtra("clinicId", clinicId);
                    startActivity(i);
                }
            });
        }
    }

    private void showClinic(ClinicResponse clinic) {
        TextView name = findViewById(R.id.clinic_name);
        TextView slogan = findViewById(R.id.clinic_slogan);
        TextView description = findViewById(R.id.clinic_description);
        LinearLayout serviceContainer = findViewById(R.id.service_packages_container);
        if (name != null) name.setText(clinic.getName());
        if (slogan != null) slogan.setText(clinic.getSlogan());
        if (description != null) description.setText(clinic.getDescription());
        if (serviceContainer != null) {
            serviceContainer.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(this);
            if (clinic.getServicePackages() != null && !clinic.getServicePackages().isEmpty()) {
                for (ServicePackage sp : clinic.getServicePackages()) {
                    View item = inflater.inflate(R.layout.service_package_item, serviceContainer, false);
                    ((TextView) item.findViewById(R.id.service_name)).setText(sp.getName());
                    ((TextView) item.findViewById(R.id.service_description)).setText(sp.getDescription());
                    ((TextView) item.findViewById(R.id.service_price)).setText("Giá: " + sp.getPrice() + " VND");
                    serviceContainer.addView(item);
                }
            } else {
                TextView empty = new TextView(this);
                empty.setText("Không có gói dịch vụ nào.");
                serviceContainer.addView(empty);
            }
        }
    }
} 